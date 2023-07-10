package com.pcdev.expensemanagerbackend.repository;

import com.pcdev.expensemanagerbackend.model.transaction.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
@Transactional
public interface TransactionRepository extends MongoRepository<Transaction, String> {
    @Query("{'user.userId': ?0}")
    List<Transaction> findAllByUserId(String userId);

    @Query("{'user.userId': ?0, 'transactionDate': { $gte: ?1, $lte: ?2 }}")
    List<Transaction> findAllByUserIdAndTransactionDateBetween(String userId, LocalDate startDate, LocalDate endDate);

}
