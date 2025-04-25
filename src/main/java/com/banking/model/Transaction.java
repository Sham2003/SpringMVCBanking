package com.banking.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountNumber;           // General reference for querying
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private String type;                    // "deposit", "withdraw", "transfer"
    private double amount;
    private String description;
    private LocalDateTime timestamp;

}
