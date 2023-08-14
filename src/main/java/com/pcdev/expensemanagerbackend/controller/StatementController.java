package com.pcdev.expensemanagerbackend.controller;

import com.pcdev.expensemanagerbackend.model.transaction.dto.TransactionResponse;
import com.pcdev.expensemanagerbackend.service.statement.StatementService;
import com.pcdev.expensemanagerbackend.service.transaction.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/statement")
@RequiredArgsConstructor
public class StatementController {

    private final TransactionService transactionService;
    private final StatementService statementService;

    @GetMapping(value = "/download", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> generateStatement(
            HttpServletRequest mutableHttpServletRequest,
            @RequestParam("startDate") @DateTimeFormat(pattern = "MMM dd, yyyy") String stringStartDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "MMM dd, yyyy") String stringEndDate,
            @RequestParam("fileType") String fileType
    ) {

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

        ByteArrayInputStream byteArrayInputStream = statementService.generateStatement(
                transactionResponses,
                stringStartDate,
                stringEndDate
        );

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=employees.pdf");

        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(byteArrayInputStream));
    }
}
