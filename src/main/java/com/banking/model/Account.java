package com.banking.model;

import jakarta.persistence.*;
import lombok.*;



@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "account")
@Data
public class Account extends BaseAccount {

    private String accountNumber;
    private Double balance = 0.0;
    private String transactionPassword;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public String toString() {
        return "Account [accountNumber=" + accountNumber + ", balance=" + balance + ", transactionPassword=" + transactionPassword + ", user=" + user.getId() + "]";
    }
}
