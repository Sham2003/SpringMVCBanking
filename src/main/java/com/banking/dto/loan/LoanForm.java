// src/main/java/com/example/sample/dto/LoanForm.java
package com.banking.dto.loan;

import jakarta.validation.constraints.*;
import lombok.Data;


@Data
public class LoanForm {

    @NotBlank
    private String email;

    @Min(0) @Max(10)
    private int noOfDependents;

    @NotBlank
    private String education;

    @NotBlank
    private String selfEmployed;

    @Positive
    private double incomeAnnum;

    @Positive
    private double loanAmount;

    @Positive
    private int loanTerm;

    @Min(300) @Max(900)
    private int cibilScore;

    @Positive
    private double residentialAssetsValue;
    @Positive
    private double commercialAssetsValue;
    @Positive
    private double luxuryAssetsValue;
    @Positive
    private double bankAssetValue;

}
