// src/main/java/com/example/sample/dto/LoanForm.java
package com.banking.dto;

import jakarta.validation.constraints.*;

public class LoanForm {

    @NotBlank
    private String loanId;

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

    // --- getters & setters ---
    public String getLoanId() { return loanId; }
    public void setLoanId(String loanId) { this.loanId = loanId; }

    public int getNoOfDependents() { return noOfDependents; }
    public void setNoOfDependents(int noOfDependents) { this.noOfDependents = noOfDependents; }

    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }

    public String getSelfEmployed() { return selfEmployed; }
    public void setSelfEmployed(String selfEmployed) { this.selfEmployed = selfEmployed; }

    public double getIncomeAnnum() { return incomeAnnum; }
    public void setIncomeAnnum(double incomeAnnum) { this.incomeAnnum = incomeAnnum; }

    public double getLoanAmount() { return loanAmount; }
    public void setLoanAmount(double loanAmount) { this.loanAmount = loanAmount; }

    public int getLoanTerm() { return loanTerm; }
    public void setLoanTerm(int loanTerm) { this.loanTerm = loanTerm; }

    public int getCibilScore() { return cibilScore; }
    public void setCibilScore(int cibilScore) { this.cibilScore = cibilScore; }

    public double getResidentialAssetsValue() { return residentialAssetsValue; }
    public void setResidentialAssetsValue(double residentialAssetsValue) { this.residentialAssetsValue = residentialAssetsValue; }

    public double getCommercialAssetsValue() { return commercialAssetsValue; }
    public void setCommercialAssetsValue(double commercialAssetsValue) { this.commercialAssetsValue = commercialAssetsValue; }

    public double getLuxuryAssetsValue() { return luxuryAssetsValue; }
    public void setLuxuryAssetsValue(double luxuryAssetsValue) { this.luxuryAssetsValue = luxuryAssetsValue; }

    public double getBankAssetValue() { return bankAssetValue; }
    public void setBankAssetValue(double bankAssetValue) { this.bankAssetValue = bankAssetValue; }
}
