package com.banking.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@MappedSuperclass
@Data
public abstract class BaseAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String mobileNumber;
    private LocalDate dob;
    private String accountType;
    private LocalDateTime createdOn = LocalDateTime.now();
}
