package com.cd_u.catatdulu_users.controller;

import com.cd_u.catatdulu_users.model.UserModel;
import com.cd_u.catatdulu_users.service.*;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final ExcelService excelService;
    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final EmailForwadService emailForwadService;
    private final UserService userService;

    @GetMapping("/income-excel")
    public ResponseEntity<Void> emailIncomeExcel() throws IOException, MessagingException {
        UserModel user = userService.getCurrentProfile();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        excelService.writeIncomesToExcel(baos, incomeService.getCurrentMonthIncomesForCurrentUser());
        emailForwadService.sendEmailWithAttachment(user.getEmail(),
                "Your Income Report",
                "This here are the list report of your income",
                baos.toByteArray(),
                "income_report.xlsx");
        return ResponseEntity.ok(null);
    }

    @GetMapping("/expense-excel")
    public ResponseEntity<Void> emailExpenseExcel() throws IOException, MessagingException {
        UserModel user = userService.getCurrentProfile();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        excelService.writeExpensesToExcel(baos, expenseService.getCurrentMonthExpensesForCurrentUser());
        emailForwadService.sendEmailWithAttachment(user.getEmail(),
                "Your Expense Report",
                "This here are the list report of your expense",
                baos.toByteArray(),
                "expense_report.xlsx");
        return ResponseEntity.ok(null);
    }
}
