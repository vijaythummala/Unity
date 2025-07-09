package com.unity.payment_service.controller;

import com.unity.payment_service.agent.PaymentFileParser;
import com.unity.payment_service.dto.PaymentDTO;
import com.unity.payment_service.dto.PaymentInfoDTO;
import com.unity.payment_service.dto.ScheduledPaymentDTO;
import com.unity.payment_service.service.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentFileParser paymentFileParser;


    @PostMapping("/createPayment")
    public ResponseEntity<String> createPayment(@RequestBody PaymentDTO paymentDTO) {
        String response = paymentService.createPayment(paymentDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/schedule")
    public ResponseEntity<String> createScheduledPayment(@RequestBody ScheduledPaymentDTO scheduledPaymentDTO) {
        String response = paymentService.createScheduledPayment(scheduledPaymentDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/view")
    public ResponseEntity<List<PaymentDTO>> getPayments(
            @RequestParam Long userId,
            @RequestParam Long bankAccountId,
            @RequestParam int page,
            @RequestParam int limit) {

        List<PaymentDTO> payments = paymentService.getPayments(userId, bankAccountId, page, limit);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/view-scheduled")
    public ResponseEntity<List<ScheduledPaymentDTO>> getScheduledPayments(
            @RequestParam Long userId,
            @RequestParam Long bankAccountId,
            @RequestParam int page,
            @RequestParam int limit) {

        List<ScheduledPaymentDTO> scheduledPayments = paymentService.getScheduledPayments(userId, bankAccountId, page, limit);
        return ResponseEntity.ok(scheduledPayments);
    }

    @DeleteMapping("/delete-scheduled")
    public ResponseEntity<String> deleteScheduledPayment(
            @RequestParam Long userId,
            @RequestParam Long bankAccountId,
            @RequestParam Long paymentId) {

        String response = paymentService.deleteScheduledPayment(userId, bankAccountId, paymentId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadPaymentFile(@RequestParam("file") MultipartFile file) {
        try {
            List<PaymentInfoDTO> paymentDTOs = paymentFileParser.parse(file);
            paymentService.processPayments(paymentDTOs);
            return ResponseEntity.ok("File processed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }
}
