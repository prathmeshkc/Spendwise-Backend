package com.pcdev.expensemanagerbackend.controller;

import com.pcdev.expensemanagerbackend.model.auth.MessageBody;
import com.pcdev.expensemanagerbackend.model.transaction.dto.TransactionRequest;
import com.pcdev.expensemanagerbackend.model.transaction.dto.TransactionResponse;
import com.pcdev.expensemanagerbackend.service.transaction.TransactionService;
import com.pcdev.expensemanagerbackend.util.exceptions.BadRequestException;
import com.pcdev.expensemanagerbackend.util.exceptions.InternalServerErrorException;
import com.pcdev.expensemanagerbackend.util.exceptions.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<?> createTransaction(
            HttpServletRequest mutableHttpServletRequest,
            @RequestBody TransactionRequest transactionRequest
    ) {
        System.out.println("Inside createTransaction");
        try {
            String userId = mutableHttpServletRequest.getHeader("userId");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH);
            LocalDate localDate = LocalDate.parse(transactionRequest.getTransactionDate(), formatter);
            Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            TransactionResponse transactionResponse = transactionService.createTransaction(
                    userId,
                    transactionRequest.getTitle(),
                    transactionRequest.getAmount(),
                    transactionRequest.getTransactionType(),
                    transactionRequest.getCategory(),
                    date,
                    transactionRequest.getNote()
            );

            return ResponseEntity.ok(transactionResponse);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(new MessageBody(e.getMessage()));
        } catch (InternalServerErrorException e) {
            return ResponseEntity.internalServerError().body(new MessageBody(e.getMessage()));
        }
    }


    @GetMapping
    public ResponseEntity<?> getAllTransaction(HttpServletRequest mutableHttpServletRequest) {
        System.out.println("Inside getAllTransaction");
        System.out.println(mutableHttpServletRequest.getHeader("User-Agent"));
        try {
            String userId = mutableHttpServletRequest.getHeader("userId");
            List<TransactionResponse> transactionResponses = transactionService.getAllTransactions(userId);
            return ResponseEntity.ok(transactionResponses);

        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageBody(e.getMessage()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageBody("Something went wrong in fetching transactions!"));
        }
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<?> getTransactionById(
            HttpServletRequest mutableHttpServletRequest,
            @PathVariable String transactionId
    ) {
        System.out.println("Inside getTransactionById");

        try {

            String userId = mutableHttpServletRequest.getHeader("userId");
            return ResponseEntity.ok(transactionService.getTransactionById(transactionId));

        } catch (BadRequestException badRequestException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageBody(badRequestException.getMessage()));

        } catch (NotFoundException notFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageBody(notFoundException.getMessage()));

        }
    }

    @GetMapping("/filter")
    public ResponseEntity<?> getAllTransactionBetweenDates(
            HttpServletRequest mutableHttpServletRequest,
            @RequestParam("startDate") @DateTimeFormat(pattern = "MMM dd, yyyy") String stringStartDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "MMM dd, yyyy") String stringEndDate
    ) {
        System.out.println("Inside getAllTransactionBetweenDates");

        try {
            String userId = mutableHttpServletRequest.getHeader("userId");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH);
            LocalDate startlocalDate = LocalDate.parse(stringStartDate, formatter);
            LocalDate endlocalDate = LocalDate.parse(stringEndDate, formatter);
            Date startDate = Date.from(startlocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(endlocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            System.out.println("StartDate: " + startDate);
            System.out.println("EndDate: " + endDate);

            List<TransactionResponse> transactionResponses = transactionService.getAllTransactionBetweenDates(
                    userId,
                    startDate,
                    endDate
            );

            return ResponseEntity.ok(transactionResponses);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageBody(e.getMessage()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageBody("Something went wrong in fetching transaction between dates!"));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchTransactionsByText(
            HttpServletRequest mutableHttpServletRequest,
            @RequestParam(name = "searchQuery") String searchText
    ) {
        System.out.println("Inside searchTransactionsByText");

        try {
            String userId = mutableHttpServletRequest.getHeader("userId");
            if (searchText == null || searchText.isEmpty()) {
                return ResponseEntity.ok(new ArrayList<TransactionResponse>());
            }
            List<TransactionResponse> transactionResponses = transactionService.searchByText(userId, searchText);
            return ResponseEntity.ok(transactionResponses);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageBody(e.getMessage()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageBody("Something went wrong in searching transactions by text!"));
        }
    }

    @GetMapping("/search/type")
    public ResponseEntity<?> searchTransactionByTypeAndText(
            HttpServletRequest mutableHttpServletRequest,
            @RequestParam(name = "searchQuery") String searchText,
            @RequestParam(name = "type") String transactionType
    ) {
        System.out.println("Inside searchTransactionByTypeAndText");

        try {
            String userId = mutableHttpServletRequest.getHeader("userId");

            if (transactionType == null || transactionType.isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageBody("Specify Transaction Type!"));
            }

            if (searchText == null || searchText.isEmpty()) {
                return ResponseEntity.ok(new ArrayList<TransactionResponse>());
            }

            if (transactionType.equalsIgnoreCase("INCOME")) {
                transactionType = "INCOME";
            } else {
                transactionType = "EXPENSE";
            }

            List<TransactionResponse> transactionResponses = transactionService.searchByTypeAndText(userId, transactionType, searchText);
            return ResponseEntity.ok(transactionResponses);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageBody(e.getMessage()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageBody("Something went wrong in searching transactions by text and type!"));
        }
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<?> updateTransaction(
            HttpServletRequest mutableHttpServletRequest,
            @PathVariable("transactionId") String transactionId,
            @RequestBody TransactionRequest transactionRequest
    ) {
        System.out.println("Inside updateTransaction");

        try {
            String userId = mutableHttpServletRequest.getHeader("userId");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH);
            LocalDate localDate = LocalDate.parse(transactionRequest.getTransactionDate(), formatter);
            Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());


            Object transactionResponse = transactionService.updateTransaction(
                    userId,
                    transactionId,
                    transactionRequest.getTitle(),
                    transactionRequest.getAmount(),
                    transactionRequest.getTransactionType(),
                    transactionRequest.getCategory(),
                    date,
                    transactionRequest.getNote()
            );

            if (transactionResponse instanceof MessageBody) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(transactionResponse);
            } else {
                return ResponseEntity.ok(transactionResponse);
            }

        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageBody(e.getMessage()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(new MessageBody("Something went wrong in updating transaction!"));
        }
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<?> deleteTransaction(
            HttpServletRequest mutableHttpServletRequest,
            @PathVariable("transactionId") String transactionId
    ) {
        System.out.println("Inside deleteTransaction");

        try {
            String userId = mutableHttpServletRequest.getHeader("userId");
            Object transactionResponse = transactionService.deleteTransaction(
                    userId, transactionId
            );

            if (transactionResponse instanceof MessageBody) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(transactionResponse);
            } else {
                return ResponseEntity.accepted().body(transactionResponse);
            }
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageBody(e.getMessage()));
        } catch (InternalServerErrorException e) {
            return ResponseEntity.internalServerError().body(new MessageBody(e.getMessage()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(new MessageBody(e.getMessage()));
        }
    }




}
