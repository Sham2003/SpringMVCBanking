package com.banking.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account extends BaseAccount {

    private String accountNumber;
    private Double balance = 0.0;
    private String transactionPassword;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
