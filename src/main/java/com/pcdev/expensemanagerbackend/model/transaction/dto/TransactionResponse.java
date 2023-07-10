package com.pcdev.expensemanagerbackend.model.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
    private String transactionId;
    private String userId;
    private String title;
    private Double amount;
    private String transactionType;
    private String category;
    private String transactionDate;
    private String note;
}
