// src/main/java/com/example/sample/dto/LoanFeatures.java
package com.banking.dto.loan;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
public class LoanFeatures implements Serializable {

    private float noOfDependents;
    private float education;
    private float selfEmployed;
    private float incomeAnnum;
    private float loanAmount;
    private float loanTerm;
    private float cibilScore;
    private float residentialAssetsValue;
    private float commercialAssetsValue;
    private float luxuryAssetsValue;
    private float bankAssetValue;


    public LoanFeatures(LoanForm form) {
        // 1) Define your mappings (ignore np.str_, just plain Strings)
        Map<String, Integer> educationMap = Map.of(
                "Graduate",      0,
                "Not Graduate",  1
        );
        Map<String, Integer> selfEmployedMap = Map.of(
                "No",   0,
                "Yes",  1
        );

        // 2) Copy & convert
        this.noOfDependents       = form.getNoOfDependents();
        // trim() to remove any leading/trailing spaces
        this.education            = educationMap.getOrDefault(form.getEducation().trim(),      -1);
        this.selfEmployed         = selfEmployedMap.getOrDefault(form.getSelfEmployed().trim(), -1);
        this.incomeAnnum          = (float) form.getIncomeAnnum();
        this.loanAmount           = (float) form.getLoanAmount();
        this.loanTerm             = (float) form.getLoanTerm();
        this.cibilScore           = (float) form.getCibilScore();
        this.residentialAssetsValue  = (float) form.getResidentialAssetsValue();
        this.commercialAssetsValue   = (float) form.getCommercialAssetsValue();
        this.luxuryAssetsValue       = (float) form.getLuxuryAssetsValue();
        this.bankAssetValue          = (float) form.getBankAssetValue();
    }

    // Add getters and setters here

    // Add this method to convert to float array
    public float[] toFloatArray() {
        return new float[]{
                noOfDependents,
                education,
                selfEmployed,
                incomeAnnum,
                loanAmount,
                loanTerm,
                cibilScore,
                residentialAssetsValue,
                commercialAssetsValue,
                luxuryAssetsValue,
                bankAssetValue
        };
    }
}
