// src/main/java/com/example/sample/model/Loan.java
package com.banking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "loans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {

    @Id
    @Column(length = 32)
    private String loanId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    private int noOfDependents;
    private String education;
    private String selfEmployed;
    private double incomeAnnum;
    private double loanAmount;
    private int loanTerm;
    private int cibilScore;
    private double residentialAssetsValue;
    private double commercialAssetsValue;
    private double luxuryAssetsValue;
    private double bankAssetValue;
    private String approvalStatus;

    private LocalDateTime createdOn = LocalDateTime.now();
}
