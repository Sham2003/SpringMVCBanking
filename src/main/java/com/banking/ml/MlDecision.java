// src/main/java/com/example/sample/ml/MlDecision.java
package com.banking.ml;

public class MlDecision {

    private String approved;

    public MlDecision() { }     // needed by Jackson

    public String getApproved() { return approved; }
    public void setApproved(String approved) { this.approved = approved; }
}