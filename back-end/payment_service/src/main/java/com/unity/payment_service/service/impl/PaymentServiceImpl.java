package com.unity.payment_service.service.impl;

import com.unity.payment_service.dto.PaymentDTO;
import com.unity.payment_service.dto.PaymentInfoDTO;
import com.unity.payment_service.dto.ScheduledPaymentDTO;
import com.unity.payment_service.entity.BatchInfo;
import com.unity.payment_service.entity.Payment;
import com.unity.payment_service.entity.PaymentInfo;
import com.unity.payment_service.entity.ScheduledPayment;
import com.unity.payment_service.exception.PaymentException;
import com.unity.payment_service.mapper.PaymentInfoMapper;
import com.unity.payment_service.mapper.PaymentMapper;
import com.unity.payment_service.mapper.ScheduledPaymentMapper;
import com.unity.payment_service.repository.BatchInfoRepository;
import com.unity.payment_service.repository.PaymentRepository;
import com.unity.payment_service.repository.ScheduledPaymentRepository;
import com.unity.payment_service.service.PaymentService;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    ScheduledPaymentRepository scheduledPaymentRepository;

    @Autowired
    PaymentInfoMapper paymentInfoMapper;

    @Autowired
    BatchInfoRepository batchRepo; 

    @Override
    @Transactional
    public String createPayment(PaymentDTO paymentDTO) {
        Payment payment = PaymentMapper.toEntity(paymentDTO);
        paymentRepository.save(payment);
        return "Payment created successfully.";
    }

    @Override
    @Transactional
    public String createScheduledPayment(ScheduledPaymentDTO scheduledPaymentDTO) {
        ScheduledPayment scheduledPayment = ScheduledPaymentMapper.toEntity(scheduledPaymentDTO);
        scheduledPaymentRepository.save(scheduledPayment);
        return "Scheduled payment created successfully.";
    }

    @Override
    public List<PaymentDTO> getPayments(Long userId, Long bankAccountId, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        List<Payment> payments = paymentRepository.findByUserIdAndBankAccountId(userId, bankAccountId, pageable);

        if (payments.isEmpty()) {
            throw new PaymentException("No payments found.");
        }

        return payments.stream().map(PaymentMapper::toDTO).toList();
    }

    @Override
    public List<ScheduledPaymentDTO> getScheduledPayments(Long userId, Long bankAccountId, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        List<ScheduledPayment> scheduledPayments = scheduledPaymentRepository.findByUserIdAndBankAccountId(userId,
                bankAccountId, pageable);

        if (scheduledPayments.isEmpty()) {
            throw new PaymentException("No scheduled payments found.");
        }

        return scheduledPayments.stream().map(ScheduledPaymentMapper::toDTO).toList();
    }

    @Override
    @Transactional
    public String deleteScheduledPayment(Long userId, Long bankAccountId, Long paymentId) {
        ScheduledPayment scheduledPayment = scheduledPaymentRepository
                .findByIdAndUserIdAndBankAccountId(paymentId, userId, bankAccountId)
                .orElseThrow(() -> new PaymentException("Scheduled payment not found."));

        scheduledPayment.setDeleted(true);
        scheduledPaymentRepository.save(scheduledPayment);

        return "Scheduled payment deleted successfully.";
    }

    @Override
    @Transactional
    public void processPayments(List<PaymentInfoDTO> payments) {
        // Group payments based on a batch key (using any combination of fields)
        Map<String, List<PaymentInfoDTO>> groupedData = payments.stream()
                .collect(Collectors.groupingBy(dto -> generateBatchKey(dto)));

        // Create a fixed thread pool; pool size can be tuned based on expected load
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        List<Future<?>> futures = new ArrayList<>();
        // For each group, submit a Runnable task to process and save the batch concurrently
        for (Map.Entry<String, List<PaymentInfoDTO>> entry : groupedData.entrySet()) {
            futures.add(executorService.submit(() -> {
                List<PaymentInfoDTO> group = entry.getValue();
                String bankName = group.get(0).getBankName();
                BatchInfo batch = new BatchInfo();
                batch.setCreatedAt(LocalDateTime.now());
                batch.setBankName(bankName);
                List<PaymentInfo> validPayments = new ArrayList<>();
                List<String> errorMessages = new ArrayList<>();

                for (PaymentInfoDTO dto : group) {
                    try {
                        // Custom validation logic; throws ValidationException on error
                        validate(dto);
                        PaymentInfo payment = paymentInfoMapper.toEntity(dto);
                        payment.setBatchInfo(batch);
                        validPayments.add(payment);
                    } catch (ValidationException ex) {
                        errorMessages.add("GUID " + dto.getGuid() + ": " + ex.getMessage());
                    }
                }

                // Save the batch with valid payments, or mark as failed if none are valid
                if (!validPayments.isEmpty()) {
                    batch.setSuccessful(errorMessages.isEmpty());
                    if (!errorMessages.isEmpty()) {
                        batch.setErrorMessage(String.join("; ", errorMessages));
                    }
                    batch.setPayments(validPayments);
                } else {
                    batch.setSuccessful(false);
                    batch.setErrorMessage("No valid payments in this batch. Errors: " + String.join("; ", errorMessages));
                }
                batchRepo.save(batch);
            }));
        }

        // Wait for all tasks to complete
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception ex) {
                // Log or handle exceptions here as needed
                ex.printStackTrace();
            }
        }
        executorService.shutdown();
    }

    private void validate(PaymentInfoDTO dto) {
        if (dto.getPaymentAmount() == null || dto.getPaymentAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Invalid payment amount");
        }
        if (dto.getPayerEmail() == null || !dto.getPayerEmail().contains("@")) {
            throw new ValidationException("Invalid payer email");
        }
        // Add more validations as needed
    }

    private String generateBatchKey(PaymentInfoDTO dto) {
        return String.join("|",
                dto.getBankName(),
                dto.getPaymentMethod(),
                dto.getPayeeCountry(),
                dto.getPaymentCurrency());
    }
}
