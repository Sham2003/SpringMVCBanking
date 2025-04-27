package com.banking.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pending_account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PendingAccount extends BaseAccount {
    private String password;
    private String otp; // Extra field for verification

    public Account checkout() {
        Account account = new Account();

        account.setName(this.getName());
        account.setEmail(this.getEmail());
        account.setMobileNumber(this.getMobileNumber());
        account.setDob(this.getDob());
        account.setAccountType(this.getAccountType());
        account.setBalance(0.0);
        account.setCreatedOn(this.getCreatedOn());

        return account;
    }

    // You can add status or expiry here too
}
