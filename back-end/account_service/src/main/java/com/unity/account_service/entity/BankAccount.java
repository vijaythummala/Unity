package com.unity.account_service.entity;

import com.unity.account_service.constants.AccountStatus;
import com.unity.account_service.constants.AccountType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bank_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 12, nullable = false, updatable = false)
    private String accountNumber;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @Column(nullable = false)
    private double balance = 0.0;

    @Column(nullable = false)
    private String currency = "USD"; // Only USD is supported
}
