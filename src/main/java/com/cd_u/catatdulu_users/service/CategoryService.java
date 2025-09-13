package com.cd_u.catatdulu_users.service;

import com.cd_u.catatdulu_users.dto.CategoryDTO;
import com.cd_u.catatdulu_users.model.CategoryModel;
import com.cd_u.catatdulu_users.model.UserModel;
import com.cd_u.catatdulu_users.repository.CategoryRepository;
import com.cd_u.catatdulu_users.repository.ExpenseRepository;
import com.cd_u.catatdulu_users.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final UserService userService;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;

    private CategoryModel toModel(CategoryDTO categoryDTO, UserModel user) {
        return CategoryModel.builder()
                .name(categoryDTO.getName())
                .icon(categoryDTO.getIcon())
                .profile(user)
                .type(categoryDTO.getType())
                .build();
    }

    private CategoryDTO toDTO(CategoryModel model) {
        return CategoryDTO.builder()
                .id(model.getId())
                .profileId(model.getProfile() != null ? model.getProfile().getId(): null)
                .name(model.getName())
                .icon(model.getIcon())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .type(model.getType())
                .build();
    }

    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        UserModel profile = userService.getCurrentProfile();
        if (categoryRepository.existsByNameAndProfileId(categoryDTO.getName(), profile.getId())) {
            throw new RuntimeException("Category with this name already exists");
        }

        CategoryModel newCategory = toModel(categoryDTO, profile);
        newCategory = categoryRepository.save(newCategory);
        return toDTO(newCategory);
    }

    public List<CategoryDTO> getCategoriesForCurrentUser() {
        UserModel user = userService.getCurrentProfile();
        List<CategoryModel> categories = categoryRepository.findByProfileId(user.getId());
        return categories.stream().map(this::toDTO).toList();
    }

    public List<CategoryDTO> getCategoriesByTypeForCurrentUser(String type) {
        UserModel user = userService.getCurrentProfile();
        List<CategoryModel> entities = categoryRepository.findByTypeAndProfileId(type, user.getId());
        return entities.stream().map(this::toDTO).toList();
    }

    public CategoryDTO updateCategory(Long categoryId, CategoryDTO dto) {
        UserModel user = userService.getCurrentProfile();
        CategoryModel existingCategory = categoryRepository.findByIdAndProfileId(categoryId, user.getId())
                .orElseThrow(() -> new RuntimeException("Category is not accessible"));
        existingCategory.setName(dto.getName());
        existingCategory.setType(dto.getType());
        existingCategory.setIcon(dto.getIcon());
        existingCategory = categoryRepository.save(existingCategory);
        return toDTO(existingCategory);
    }

    public CategoryDTO getCategoryById(Long categoryId) {
        UserModel user = userService.getCurrentProfile();
        CategoryModel category = categoryRepository.findByIdAndProfileId(categoryId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found or accessible"));
        return toDTO(category);
    }

    //public void deleteCategory(Long categoryId) {
        //UserModel user = userService.getCurrentProfile();
       // CategoryModel category = categoryRepository.findByIdAndProfileId(categoryId, user.getId())
                //.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found or accessible"));

        //categoryRepository.delete(category);
    //}
}
