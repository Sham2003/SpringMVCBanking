// src/main/java/com/example/sample/model/Loan.java
package com.banking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "loans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    @Id
    @Column(length = 32)
    private String loanId;

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

}
