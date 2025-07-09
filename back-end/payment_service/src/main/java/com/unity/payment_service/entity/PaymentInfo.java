package com.unity.payment_service.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "payment_info")
@Data
public class PaymentInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String guid;
    private String payerName;
    private String payerEmail;
    private String payerCountry;
    private String payerAddress;
    private String payerCity;
    private String payerState;
    private String payerPostalCode;

    private String payeeName;
    private String payeeEmail;
    private String payeeCountry;
    private String payeeAddress;
    private String payeeCity;
    private String payeeState;
    private String payeePostalCode;

    private BigDecimal paymentAmount;
    private LocalDate paymentDate;
    private String paymentMethod;
    private String paymentStatus;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private String paymentReference;
    private String paymentNotes;
    private String paymentApprovalStatus;
    private LocalDate paymentApprovalDate;
    private String paymentApprovalNotes;
    private String paymentProcessingTime;
    private String paymentCurrency;
    private BigDecimal paymentExchangeRate;

    private String bankName;

    @ManyToOne
    @JoinColumn(name = "batch_id")
    private BatchInfo batchInfo;
}
