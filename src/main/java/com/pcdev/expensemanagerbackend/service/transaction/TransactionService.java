package com.pcdev.expensemanagerbackend.service.transaction;

import com.pcdev.expensemanagerbackend.model.User.User;
import com.pcdev.expensemanagerbackend.model.auth.MessageBody;
import com.pcdev.expensemanagerbackend.model.transaction.Transaction;
import com.pcdev.expensemanagerbackend.model.transaction.dto.DeletedTransactionResponse;
import com.pcdev.expensemanagerbackend.model.transaction.dto.TransactionResponse;
import com.pcdev.expensemanagerbackend.repository.TransactionRepository;
import com.pcdev.expensemanagerbackend.util.Helper;
import com.pcdev.expensemanagerbackend.util.exceptions.BadRequestException;
import com.pcdev.expensemanagerbackend.util.exceptions.InternalServerErrorException;
import com.pcdev.expensemanagerbackend.util.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionResponse createTransaction(
            String userId,
            String title,
            Double amount,
            String transactionType,
            String category,
            Date transactionDate,
            String note
    ) {

        if (userId == null) {
            throw new BadRequestException("User Not Found!");
        }

        User user = User.builder()
                .userId(userId)
                .build();


        Transaction transaction = Transaction.builder()
                .title(title)
                .amount(amount)
                .transactionType(transactionType)
                .category(category)
                .transactionDate(transactionDate)
                .note(note)
                .user(user)
                .build();

        try {
            Transaction savedTransaction = transactionRepository.save(transaction);

/*return TransactionResponse.builder()
                    .transactionId(savedTransaction.getTransactionId())
                    .userId(userId)
                    .title(savedTransaction.getTitle())
                    .amount(savedTransaction.getAmount())
                    .transactionType(savedTransaction.getTransactionType())
                    .category(savedTransaction.getCategory())
                    .transactionDate(formatter.format(localDate))
                    .note(savedTransaction.getNote())
                    .build();
*/
            return Helper.getTransactionResponseFromTransaction(savedTransaction);
        } catch (Exception exception) {
            System.out.println("Exception occurred while saving Transaction: " + exception.getMessage());
            throw new InternalServerErrorException("Something Went Wrong In Creating Transaction!");
        }
    }

    public List<TransactionResponse> getAllTransactions(String userId) {
        if (userId == null) {
            throw new BadRequestException("User Not Found!");
        }

        List<Transaction> transactions = transactionRepository.findAllByUserId(userId);
        return Helper.getTransactionResponses(transactions);
    }

    public TransactionResponse getTransactionById(String transactionId) {
        if (transactionId == null) {
            throw new BadRequestException("Transaction Not Found!");
        }

        Optional<Transaction> transaction = transactionRepository.findById(transactionId);
        if (transaction.isEmpty()) {
            throw new NotFoundException("Transaction Not Found!");
        }

        return Helper.getTransactionResponseFromTransaction(transaction.get());

    }

    public List<TransactionResponse> getAllTransactionBetweenDates(
            String userId,
            Date startDate,
            Date endDate
    ) {
        if (userId == null) {
            throw new BadRequestException("User Not Found!");
        }

        List<Transaction> transactionsBetweenDates = transactionRepository.findAllByUserIdAndTransactionDateBetween(
                userId, startDate, endDate);

        return Helper.getTransactionResponses(transactionsBetweenDates);
    }

    public List<TransactionResponse> searchByText(
            String userId,
            String searchText
    ) {
        if (userId == null) {
            throw new BadRequestException("User Not Found!");
        }

        List<Transaction> foundTransactions = transactionRepository.searchByUserIdAndText(userId, searchText);
        return Helper.getTransactionResponses(foundTransactions);
    }

    public List<TransactionResponse> searchByTypeAndText(
            String userId,
            String transactionType,
            String searchText
    ) {
        if (userId == null) {
            throw new BadRequestException("User Not Found!");
        }

        List<Transaction> foundTransactions = transactionRepository.searchByUserIdAndTypeAndText(userId, transactionType, searchText);
        return Helper.getTransactionResponses(foundTransactions);
    }

    public Object updateTransaction(
            String userId,
            String transactionId,
            String title,
            Double amount,
            String transactionType,
            String category,
            Date transactionDate,
            String note
    ) {
        if (userId == null) {
            throw new BadRequestException("User Not Found!");
        }

        Optional<Transaction> transactionInDBOptional = transactionRepository.findById(transactionId);
        if (transactionInDBOptional.isEmpty()) {
            throw new NotFoundException("No Transaction Found!");
        }

        if (transactionInDBOptional.get().getUser().getUserId().equals(userId)) {
            Transaction newTransaction = transactionInDBOptional.get();
            newTransaction.setTitle(title);
            newTransaction.setAmount(amount);
            newTransaction.setTransactionType(transactionType);
            newTransaction.setCategory(category);
            newTransaction.setTransactionDate(transactionDate);
            newTransaction.setNote(note);

            try {
                Transaction updatedTransaction = transactionRepository.save(newTransaction);
                return Helper.getTransactionResponseFromTransaction(updatedTransaction);

                /*return TransactionResponse.builder()
                        .transactionId(updatedTransaction.getTransactionId())
                        .userId(updatedTransaction.getUser().getUserId())
                        .title(updatedTransaction.getTitle())
                        .amount(updatedTransaction.getAmount())
                        .transactionType(updatedTransaction.getTransactionType())
                        .category(updatedTransaction.getCategory())
                        .transactionDate(updatedTransaction.getTransactionDate())
                        .note(updatedTransaction.getNote())
                        .build();*/

            } catch (Exception e) {
                throw new InternalServerErrorException("Something went wrong in updating transaction!");
            }
        } else {
            return new MessageBody("Unauthorized to update transaction!");
        }
    }

    public Object deleteTransaction(
            String userId,
            String transactionId
    ) {
        if (userId == null) {
            throw new BadRequestException("User Not Found!");
        }

        Optional<Transaction> transactionInDBOptional = transactionRepository.findById(transactionId);
        if (transactionInDBOptional.isEmpty()) {
            throw new NotFoundException("No Transaction Found!");
        }

        if (transactionInDBOptional.get().getUser().getUserId().equals(userId)) {
            Transaction deletedTransaction = transactionInDBOptional.get();
            transactionRepository.deleteById(transactionId);

            /*TransactionResponse transactionResponse = TransactionResponse.builder()
                    .transactionId(deletedTransaction.getTransactionId())
                    .userId(deletedTransaction.getUser().getUserId())
                    .title(deletedTransaction.getTitle())
                    .amount(deletedTransaction.getAmount())
                    .transactionType(deletedTransaction.getTransactionType())
                    .category(deletedTransaction.getCategory())
                    .transactionDate(deletedTransaction.getTransactionDate())
                    .note(deletedTransaction.getNote())
                    .build();
*/

            TransactionResponse transactionResponse = Helper.getTransactionResponseFromTransaction(deletedTransaction);

            return DeletedTransactionResponse.builder()
                    .message("Transaction Deleted Successfully!")
                    .deleted_Transaction(transactionResponse)
                    .build();

        } else {
            return new MessageBody("Unauthorized to delete transaction!");
        }

    }



}


