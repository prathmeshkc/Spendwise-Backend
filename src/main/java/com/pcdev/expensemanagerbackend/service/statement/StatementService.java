package com.pcdev.expensemanagerbackend.service.statement;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.pcdev.expensemanagerbackend.model.transaction.dto.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StatementService {


    public ByteArrayInputStream generateStatement(
            List<TransactionResponse> transactionResponses,
            String stringStartDate,
            String stringEndDate
    ) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (Document document = new Document()) {

            PdfWriter.getInstance(document, out);
            document.open();

            /*Add Heading to the Statement*/
            Font fontHeader = FontFactory.getFont(FontFactory.TIMES_BOLD, 22);
            Paragraph para = new Paragraph("Statement for " + stringStartDate + " - " + stringEndDate, fontHeader);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(5);

            /*Add PDF Table Header*/
            Stream.of("Txn Date", "Description", "Note", "Expense", "Income").forEach(headerTitle -> {
                PdfPCell header = new PdfPCell();
                Font headFont = FontFactory.getFont(FontFactory.TIMES_BOLD);
                header.setBackgroundColor(Color.WHITE);
                header.setVerticalAlignment(Element.ALIGN_MIDDLE);
                header.setHorizontalAlignment(Element.ALIGN_CENTER);
                header.setBorderWidth(2);
                header.setPadding(8);
                header.setPhrase(new Phrase(headerTitle, headFont));
                table.addCell(header);
            });

            for (TransactionResponse transactionResponse : transactionResponses) {
                /*Txn Date*/
                PdfPCell txnDate = new PdfPCell(new Phrase(String.valueOf(transactionResponse.getTransactionDate())));
                txnDate.setPaddingLeft(4);
                txnDate.setVerticalAlignment(Element.ALIGN_MIDDLE);
                txnDate.setHorizontalAlignment(Element.ALIGN_CENTER);
                txnDate.setPadding(8);
                table.addCell(txnDate);

                /*Description*/
                PdfPCell description = new PdfPCell(new Phrase(String.valueOf(transactionResponse.getTitle())));
                description.setPaddingLeft(4);
                description.setVerticalAlignment(Element.ALIGN_MIDDLE);
                description.setHorizontalAlignment(Element.ALIGN_CENTER);
                description.setPadding(8);
                table.addCell(description);

                /*Note*/
                PdfPCell note = new PdfPCell(new Phrase(String.valueOf(transactionResponse.getNote())));
                note.setPaddingLeft(4);
                note.setVerticalAlignment(Element.ALIGN_MIDDLE);
                note.setHorizontalAlignment(Element.ALIGN_CENTER);
                note.setPadding(8);
                table.addCell(note);


                /*Expense and Income*/

                String transactionType = transactionResponse.getTransactionType();
                String amount = String.valueOf(transactionResponse.getAmount());

                PdfPCell expenseCell = new PdfPCell();
                PdfPCell incomeCell = new PdfPCell();

                expenseCell.setPaddingLeft(4);
                expenseCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                expenseCell.setHorizontalAlignment(Element.ALIGN_CENTER);

                incomeCell.setPaddingLeft(4);
                incomeCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                incomeCell.setHorizontalAlignment(Element.ALIGN_CENTER);

                if (transactionType.equals("EXPENSE")) {
                    expenseCell.setPhrase(new Phrase(amount));
                    incomeCell.setPhrase(new Phrase("\t"));
                } else if (transactionType.equals("INCOME")) {
                    expenseCell.setPhrase(new Phrase("\t"));
                    incomeCell.setPhrase(new Phrase(amount));
                } else {
                    expenseCell.setPhrase(new Phrase("\t"));
                    incomeCell.setPhrase(new Phrase("\t"));
                }

                table.addCell(expenseCell);
                table.addCell(incomeCell);
            }

            document.add(table);
        } catch (DocumentException documentException) {
            System.out.println("Document Exception: " + documentException.getMessage());
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

}
