package com.unity.payment_service.entity;

import com.unity.payment_service.constants.PaymentType;
import com.unity.payment_service.constants.RecurringType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "scheduled_payments")
public class ScheduledPayment extends BaseEntity {

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
    private RecurringType recurringType = RecurringType.NONE;

    @Column(nullable = false)
    private LocalDateTime paymentDate;
}
