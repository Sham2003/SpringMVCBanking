package com.banking.dto.transaction;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionResponse {
    String message;
    double senderBalance;
}
