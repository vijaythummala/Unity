package com.unity.core_service.repository;

import com.unity.core_service.entity.ServiceConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceConfigurationRepository extends JpaRepository<ServiceConfiguration, Long> {
    // JpaRepository provides the findAll() method
}
