package com.cd_u.catatdulu_users.controller;

import com.cd_u.catatdulu_users.dto.IncomeDTO;
import com.cd_u.catatdulu_users.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/incomes")
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping("/add")
    public ResponseEntity<IncomeDTO> addIncome(@RequestBody IncomeDTO dto) {
        IncomeDTO saved = incomeService.addIncome(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/get")
    public ResponseEntity<List<IncomeDTO>> getIncomeByMonthlyForCurrentUser() {
        List<IncomeDTO> incomes = incomeService.getCurrentMonthIncomesForCurrentUser();
        return ResponseEntity.ok(incomes);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteIncomes(@PathVariable Long id) {
        incomeService.deleteIncomes(id);
        return ResponseEntity.noContent().build();
    }
}
