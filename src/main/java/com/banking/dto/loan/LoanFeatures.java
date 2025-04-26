// src/main/java/com/example/sample/dto/LoanFeatures.java
package com.banking.dto.loan;

import java.io.Serializable;

public final class LoanFeatures implements Serializable {

    private final int noOfDependents;
    private final double incomeAnnum;
    private final double loanAmount;
    private final int loanTerm;
    private final int cibilScore;
    private final double residentialAssetsValue;
    private final double commercialAssetsValue;
    private final double luxuryAssetsValue;
    private final double bankAssetValue;

    public LoanFeatures(int noOfDependents,
                        double incomeAnnum,
                        double loanAmount,
                        int loanTerm,
                        int cibilScore,
                        double residentialAssetsValue,
                        double commercialAssetsValue,
                        double luxuryAssetsValue,
                        double bankAssetValue) {
        this.noOfDependents = noOfDependents;
        this.incomeAnnum = incomeAnnum;
        this.loanAmount = loanAmount;
        this.loanTerm = loanTerm;
        this.cibilScore = cibilScore;
        this.residentialAssetsValue = residentialAssetsValue;
        this.commercialAssetsValue = commercialAssetsValue;
        this.luxuryAssetsValue = luxuryAssetsValue;
        this.bankAssetValue = bankAssetValue;
    }

    // -------- getters --------
    public int getNoOfDependents()     { return noOfDependents; }
    public double getIncomeAnnum()     { return incomeAnnum; }
    public double getLoanAmount()      { return loanAmount; }
    public int getLoanTerm()           { return loanTerm; }
    public int getCibilScore()         { return cibilScore; }
    public double getResidentialAssetsValue() { return residentialAssetsValue; }
    public double getCommercialAssetsValue()  { return commercialAssetsValue; }
    public double getLuxuryAssetsValue()      { return luxuryAssetsValue; }
    public double getBankAssetValue()         { return bankAssetValue; }
}
