package com.pcdev.expensemanagerbackend.util;

import com.pcdev.expensemanagerbackend.model.transaction.Transaction;
import com.pcdev.expensemanagerbackend.model.transaction.dto.TransactionResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Helper {

    public static List<TransactionResponse> getTransactionResponses(List<Transaction> transactions) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        return transactions.stream()
                .sorted(Comparator.comparing(Transaction::getTransactionDate).reversed())
                .map(transaction -> new TransactionResponse(
                        transaction.getTransactionId(),
                        transaction.getUser().getUserId(),
                        transaction.getTitle(),
                        transaction.getAmount(),
                        transaction.getTransactionType(),
                        transaction.getCategory(),
                        formatter.format(LocalDate.from(transaction.getTransactionDate().toInstant().atZone(ZoneId.of("UTC")))),
                        transaction.getNote()
                )).toList();


        /*sortTransactionsByDateDescending(transactionResponses);
        System.out.println("Sorted TransactionResponse List: " + transactionResponses);
        return transactionResponses;*/
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


    public static void sortTransactionsByDateDescending(List<TransactionResponse> transactions) {
        Collections.sort(transactions, new Comparator<TransactionResponse>() {
            final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");

            @Override
            public int compare(TransactionResponse o1, TransactionResponse o2) {
                try {
                    Date date1 = dateFormat.parse(o1.getTransactionDate());
                    Date date2 = dateFormat.parse(o2.getTransactionDate());
                    return date2.compareTo(date1); // Descending order
                } catch (ParseException e) {
                    // Handle parse exception if needed
                    System.out.println("Exception in sorting list of transactionResponse: " + e.getMessage());
                    return 0;
                }
            }
        });
    }
}
