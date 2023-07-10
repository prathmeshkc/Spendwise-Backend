package com.pcdev.expensemanagerbackend.model.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeletedTransactionResponse {
    private String message;
    private TransactionResponse deleted_Transaction;
}

