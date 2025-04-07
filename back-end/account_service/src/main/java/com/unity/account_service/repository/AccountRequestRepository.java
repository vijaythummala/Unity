package com.unity.account_service.repository;

import com.unity.account_service.entity.AccountRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface AccountRequestRepository extends JpaRepository<AccountRequest, Long> {

    @Query("SELECT a FROM AccountRequest a WHERE a.status = 'PENDING'")
    List<AccountRequest> findPendingRequests(int page, int limit);
}
