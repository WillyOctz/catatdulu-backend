package com.cd_u.catatdulu_users.service;

import com.cd_u.catatdulu_users.dto.CategoryDTO;
import com.cd_u.catatdulu_users.dto.ExpenseDTO;
import com.cd_u.catatdulu_users.model.CategoryModel;
import com.cd_u.catatdulu_users.model.ExpenseModel;
import com.cd_u.catatdulu_users.model.UserModel;
import com.cd_u.catatdulu_users.repository.CategoryRepository;
import com.cd_u.catatdulu_users.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final CategoryService categoryService;
    private final UserService userService;
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;


    // create expenses
    public ExpenseDTO addExpense(ExpenseDTO dto) {
        UserModel profile = userService.getCurrentProfile();
        CategoryModel category = categoryRepository.findById(Long.valueOf(dto.getCategoryId()))
                .orElseThrow(() -> new RuntimeException("Category not found"));
        ExpenseModel newExpense = toModel(dto, profile, category);
        newExpense = expenseRepository.save(newExpense);
        return toDTO(newExpense);
    }

    // get monthly expenses
    public List<ExpenseDTO> getCurrentMonthExpensesForCurrentUser() {
        UserModel user = userService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<ExpenseModel> list = expenseRepository.findByProfileAndDateBetween(user.getId(), startDate, endDate);
        return list.stream().map(this::toDTO).toList();
    }

    // deleting expenses
    public void deleteExpenses(Long expenseId) {
        UserModel user = userService.getCurrentProfile();
        ExpenseModel expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expenses not found"));
        if (!expense.getProfile().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to delete this!");
        }
        expenseRepository.delete(expense);
    }

    // get latest 5 expenses for current user
    public List<ExpenseDTO> getLatest5ExpensesForCurrentUser() {
        UserModel user = userService.getCurrentProfile();
        List<ExpenseModel> list = expenseRepository.findTop5ByProfileIdOrderByDateDesc(user.getId());
        return list.stream().map(this::toDTO).toList();
    }

    // get total expenses for current user
    public BigDecimal getTotalExpenseForCurrentUser() {
        UserModel user = userService.getCurrentProfile();
        BigDecimal total = expenseRepository.findTotalExpenseByProfileId(user.getId());
        return total != null ? total: BigDecimal.ZERO;
    }

    // filter expenses
    public List<ExpenseDTO> filterExpenses(LocalDate startDate, LocalDate endsDate, String keyword, Sort sort) {
        UserModel user = userService.getCurrentProfile();
        List<ExpenseModel> list = expenseRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(user.getId(), startDate, endsDate, keyword, sort);
        return list.stream().map(this::toDTO).toList();
    }

    // Notifications
    public List<ExpenseDTO> getExpensesForUserOnDate(Long profileId, LocalDate date) {
        List<ExpenseModel> list = expenseRepository.findByProfileIdAndDate(profileId, date);
        return list.stream().map(this::toDTO).toList();
    }


    private ExpenseModel toModel(ExpenseDTO dto, UserModel model, CategoryModel category) {
        return ExpenseModel.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(model)
                .category(category)
                .build();
    }

    private ExpenseDTO toDTO(ExpenseModel model) {
        return ExpenseDTO.builder()
                .id(model.getId())
                .name(model.getName())
                .icon(model.getIcon())
                .categoryId(String.valueOf(model.getCategory() != null ? model.getCategory().getId(): null))
                .categoryName(model.getCategory() != null ? model.getCategory().getName(): "N/A")
                .amount(model.getAmount())
                .date(model.getDate())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }
}
