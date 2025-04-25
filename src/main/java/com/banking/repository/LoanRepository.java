// src/main/java/com/example/sample/repository/LoanRepository.java
package com.banking.repository;

import com.banking.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
/**
 * Persistence interface for Loan entities.
 * Spring Boot generates the implementation at runtime.
 */
@Repository
public interface LoanRepository extends JpaRepository<Loan, String> {


       List<Loan> findByApprovalStatus(String status);
       boolean existsByLoanId(String loanId);
    
   

}
