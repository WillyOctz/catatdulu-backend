package com.cd_u.catatdulu_users.controller;

import com.cd_u.catatdulu_users.dto.ExpenseDTO;
import com.cd_u.catatdulu_users.dto.FilterDTO;
import com.cd_u.catatdulu_users.dto.IncomeDTO;
import com.cd_u.catatdulu_users.service.ExpenseService;
import com.cd_u.catatdulu_users.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/filter")
public class FilterController {

    private final ExpenseService expenseService;
    private final IncomeService incomeService;

    @PostMapping("/filter-by")
    public ResponseEntity<?> filterTransactions(@RequestBody FilterDTO filter) {
        LocalDate startDate = filter.getStartDate() != null ? filter.getStartDate() : LocalDate.of(1900, 1, 1);
        LocalDate endDate = filter.getEndDate() != null ? filter.getEndDate() : LocalDate.now();
        String keyword = filter.getKeyword() != null ? filter.getKeyword() : "";
        String sortField = filter.getSortField() != null ? filter.getSortField() : "date";
        Sort.Direction direction = "desc".equalsIgnoreCase(filter.getSortOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortField);
        if ("income".equals(filter.getType())) {
            List<IncomeDTO> incomes = incomeService.filterIncomes(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(incomes);
        } else if ("expense".equals(filter.getType())) {
            List<ExpenseDTO> expenses = expenseService.filterExpenses(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(expenses);
        } else {
            return ResponseEntity.badRequest().body("Invalid type. Must be 'incomes' or 'expenses'");
        }

    }
}
