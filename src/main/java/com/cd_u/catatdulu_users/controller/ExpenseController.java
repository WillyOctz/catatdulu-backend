package com.cd_u.catatdulu_users.controller;

import com.cd_u.catatdulu_users.dto.ExpenseDTO;
import com.cd_u.catatdulu_users.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping("/add")
    public ResponseEntity<ExpenseDTO> addExpense(@RequestBody ExpenseDTO dto) {
        ExpenseDTO saved = expenseService.addExpense(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/get")
    public ResponseEntity<List<ExpenseDTO>> getExpenseByMonthlyForCurrentUser() {
        List<ExpenseDTO> expenses = expenseService.getCurrentMonthExpensesForCurrentUser();
        return ResponseEntity.ok(expenses);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpenses(id);
        return ResponseEntity.noContent().build();
    }

}
