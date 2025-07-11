package com.unity.payment_service.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "batch_info")
@Data
public class BatchInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bankName;
    private LocalDateTime createdAt;

    private boolean successful;
    private String errorMessage;

    @OneToMany(mappedBy = "batchInfo", cascade = CascadeType.ALL)
    private List<PaymentInfo> payments;


    private boolean reprocessRequested = false;
    private boolean reprocessed = false;
    private LocalDateTime reprocessDate;
}
