package com.pcdev.expensemanagerbackend.model.transaction;

import com.pcdev.expensemanagerbackend.model.User.User;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;

@Data
@Builder
@Document(collection = "transaction")
public class Transaction {
    @MongoId
    private String transactionId;

    private String title;

    private Double amount;

    private String transactionType;

    private String category;

    private Date transactionDate;

    private String note;

    private User user;

}


