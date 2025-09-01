package com.cd_u.catatdulu_users.service;

import com.cd_u.catatdulu_users.dto.ExpenseDTO;
import com.cd_u.catatdulu_users.dto.IncomeDTO;
import com.cd_u.catatdulu_users.model.CategoryModel;
import com.cd_u.catatdulu_users.model.ExpenseModel;
import com.cd_u.catatdulu_users.model.IncomeModel;
import com.cd_u.catatdulu_users.model.UserModel;
import com.cd_u.catatdulu_users.repository.CategoryRepository;
import com.cd_u.catatdulu_users.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final CategoryService categoryService;
    private final IncomeRepository incomeRepository;
    private final UserService userService;
    private final CategoryRepository categoryRepository;

    // create incomes
    public IncomeDTO addIncome(IncomeDTO dto) {
        UserModel profile = userService.getCurrentProfile();
        CategoryModel category = categoryRepository.findById(Long.valueOf(dto.getCategoryId()))
                .orElseThrow(() -> new RuntimeException("Category not found"));
        IncomeModel newExpense = toModel(dto, profile, category);
        newExpense = incomeRepository.save(newExpense);
        return toDTO(newExpense);
    }

    // delete incomes
    public void deleteIncomes(Long incomeId) {
        UserModel user = userService.getCurrentProfile();
        IncomeModel expense = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Incomes not found"));
        if (!expense.getProfile().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to delete this!");
        }
        incomeRepository.delete(expense);
    }

    // getting monthly incomes
    public List<IncomeDTO> getCurrentMonthIncomesForCurrentUser() {
        UserModel user = userService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<IncomeModel> list = incomeRepository.findByProfileAndDateBetween(user.getId(), startDate, endDate);
        return list.stream().map(this::toDTO).toList();
    }

    // get 5 latest incomes
    public List<IncomeDTO> getLatest5IncomesForCurrentUser() {
        UserModel user = userService.getCurrentProfile();
        List<IncomeModel> list = incomeRepository.findTop5ByProfileIdOrderByDateDesc(user.getId());
        return list.stream().map(this::toDTO).toList();
    }

    // get total expenses for current user
    public BigDecimal getTotalIncomeForCurrentUser() {
        UserModel user = userService.getCurrentProfile();
        BigDecimal total = incomeRepository.findTotalIncomeByProfileId(user.getId());
        return total != null ? total: BigDecimal.ZERO;
    }

    // filter incomes
    public List<IncomeDTO> filterIncomes(LocalDate startDate, LocalDate endsDate, String keyword, Sort sort) {
        UserModel user = userService.getCurrentProfile();
        List<IncomeModel> list = incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(user.getId(), startDate, endsDate, keyword, sort);
        return list.stream().map(this::toDTO).toList();
    }

    private IncomeModel toModel(IncomeDTO dto, UserModel model, CategoryModel category) {
        return IncomeModel.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(model)
                .category(category)
                .build();
    }

    private IncomeDTO toDTO(IncomeModel model) {
        return IncomeDTO.builder()
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
