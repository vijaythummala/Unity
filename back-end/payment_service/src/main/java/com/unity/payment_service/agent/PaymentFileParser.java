package com.unity.payment_service.agent;

import com.unity.payment_service.dto.PaymentInfoDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class PaymentFileParser {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<PaymentInfoDTO> parse(MultipartFile file) throws Exception {
        List<PaymentInfoDTO> dtos = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] fields = line.split(",", -1); // -1 keeps empty strings
                PaymentInfoDTO dto = new PaymentInfoDTO();

                int i = 0;

                dto.setGuid((fields[i++]));
                dto.setGuid(fields[i++]);
                dto.setPayerName(fields[i++]);
                dto.setPayerEmail(fields[i++]);
                dto.setPayerCountry(fields[i++]);
                dto.setPayerAddress(fields[i++]);
                dto.setPayerCity(fields[i++]);
                dto.setPayerState(fields[i++]);
                dto.setPayerPostalCode(fields[i++]);

                dto.setPayeeName(fields[i++]);
                dto.setPayeeEmail(fields[i++]);
                dto.setPayeeCountry(fields[i++]);
                dto.setPayeeAddress(fields[i++]);
                dto.setPayeeCity(fields[i++]);
                dto.setPayeeState(fields[i++]);
                dto.setPayeePostalCode(fields[i++]);

                dto.setPaymentAmount(parseBigDecimal(fields[i++]));
                dto.setPaymentDate(parseDate(fields[i++]));
                dto.setPaymentMethod(fields[i++]);
                dto.setPaymentStatus(fields[i++]);

                dto.setInvoiceNumber(fields[i++]);
                dto.setInvoiceDate(parseDate(fields[i++]));
                dto.setDueDate(parseDate(fields[i++]));
                dto.setPaymentReference(fields[i++]);

                dto.setPaymentApprovalStatus(fields[i++]);
                dto.setPaymentApprovalDate(parseDate(fields[i++]));
                dto.setPaymentProcessingTime(fields[i++]);

                dto.setPaymentCurrency(fields[i++]);
                dto.setPaymentExchangeRate(parseBigDecimal(fields[i++]));
                dto.setBankName(fields[i]);

                dtos.add(dto);
            }
        }

        return dtos;
    }

    private Long parseLong(String input) {
        try {
            return Long.parseLong(input);
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDate parseDate(String value) {
        try {
            return (value == null || value.isBlank()) ? null : LocalDate.parse(value.trim(), formatter);
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal parseBigDecimal(String value) {
        try {
            return (value == null || value.isBlank()) ? null : new BigDecimal(value.trim());
        } catch (Exception e) {
            return null;
        }
    }
}
