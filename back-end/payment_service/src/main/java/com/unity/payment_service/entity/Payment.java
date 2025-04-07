package com.unity.payment_service.entity;

import java.time.LocalDateTime;

import com.unity.payment_service.constants.PaymentStatus;
import com.unity.payment_service.constants.PaymentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "payments")
public class Payment extends BaseEntity {

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long bankAccountId;

    @Column(nullable = false)
    private double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType;

    @Column(nullable = false)
    private String receiverDetails;

    @Column(nullable = true)
    private String receiverBankAccount;

    @Column(nullable = true)
    private String receiverBankName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime paymentDate;
}
