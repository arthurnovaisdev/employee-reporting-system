package com.mbfreire.employee_reporting.service;

import com.mbfreire.employee_reporting.dto.request.CategoryRequestDTO;
import com.mbfreire.employee_reporting.dto.response.CategoryResponseDTO;
import com.mbfreire.employee_reporting.entity.Category;
import com.mbfreire.employee_reporting.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponseDTO create(CategoryRequestDTO dto) {
        Category category = Category.builder()
                .name(dto.name())
                .active(dto.active())
                .build();

        category = categoryRepository.save(category);

        return new CategoryResponseDTO(category.getId(), category.getName(), category.isActive());
    }

    public List<CategoryResponseDTO> findAll() {
        return categoryRepository.findAll().stream()
                .map(cat -> new CategoryResponseDTO(cat.getId(), cat.getName(), cat.isActive()))
                .toList();
    }
}
