package com.unity.payment_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unity.payment_service.entity.BatchInfo;

@Repository
public interface BatchInfoRepository extends JpaRepository<BatchInfo, Long> {
}
