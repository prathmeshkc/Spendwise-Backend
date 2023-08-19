package com.pcdev.expensemanagerbackend.util;

import com.pcdev.expensemanagerbackend.model.transaction.Transaction;
import com.pcdev.expensemanagerbackend.model.transaction.dto.TransactionResponse;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Helper {

    public static List<TransactionResponse> getTransactionResponses(List<Transaction> transactions) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        return transactions.stream().map(transaction -> new TransactionResponse(
                transaction.getTransactionId(),
                transaction.getUser().getUserId(),
                transaction.getTitle(),
                transaction.getAmount(),
                transaction.getTransactionType(),
                transaction.getCategory(),
                formatter.format(LocalDate.from(transaction.getTransactionDate().toInstant().atZone(ZoneId.of("UTC")))),
                transaction.getNote()
        )).toList();
    }

    public static TransactionResponse getTransactionResponseFromTransaction(Transaction transaction) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        return TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .userId(transaction.getUser().getUserId())
                .title(transaction.getTitle())
                .amount(transaction.getAmount())
                .transactionType(transaction.getTransactionType())
                .category(transaction.getCategory())
                .transactionDate(formatter.format(LocalDate.from(transaction.getTransactionDate().toInstant().atZone(ZoneId.of("UTC")))))
                .note(transaction.getNote())
                .build();
    }
}
