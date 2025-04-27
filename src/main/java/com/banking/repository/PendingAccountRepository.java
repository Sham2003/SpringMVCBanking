package com.banking.repository;

import com.banking.model.PendingAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingAccountRepository extends JpaRepository<PendingAccount, Long> {
    PendingAccount findByEmail(String email);
}
