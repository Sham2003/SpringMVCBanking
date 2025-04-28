// src/main/java/com/example/sample/repository/LoanRepository.java
package com.banking.repository;

import com.banking.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.*;
/**
 * Persistence interface for Loan entities.
 * Spring Boot generates the implementation at runtime.
 */
@Repository
public interface LoanRepository extends JpaRepository<Loan, String> {

       @Query("SELECT l FROM Loan l WHERE l.user.email = :email")
       List<Loan> findLoansByUserEmail(@Param("email") String email);
}
