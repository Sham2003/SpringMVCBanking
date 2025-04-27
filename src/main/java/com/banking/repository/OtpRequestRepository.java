package com.banking.repository;

import com.banking.model.OtpRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OtpRequestRepository extends JpaRepository<OtpRequest, UUID> {

}
