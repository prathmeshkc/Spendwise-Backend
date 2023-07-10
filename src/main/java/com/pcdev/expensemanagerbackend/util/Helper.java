package com.pcdev.expensemanagerbackend.util;

import com.pcdev.expensemanagerbackend.model.transaction.Transaction;
import com.pcdev.expensemanagerbackend.model.transaction.dto.TransactionResponse;

import java.util.List;

public class Helper {

    public static List<TransactionResponse> getTransactionResponses(List<Transaction> transactions) {
        return transactions.stream().map(transaction -> new TransactionResponse(
                transaction.getTransactionId(),
                transaction.getUser().getUserId(),
                transaction.getTitle(),
                transaction.getAmount(),
                transaction.getTransactionType(),
                transaction.getCategory(),
                transaction.getTransactionDate(),
                transaction.getNote()
        )).toList();
    }

    public static TransactionResponse getTransactionResponseFromTransaction(Transaction transaction) {
        return TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .userId(transaction.getUser().getUserId())
                .title(transaction.getTitle())
                .amount(transaction.getAmount())
                .transactionType(transaction.getTransactionType())
                .category(transaction.getCategory())
                .transactionDate(transaction.getTransactionDate())
                .note(transaction.getNote())
                .build();
    }

}
