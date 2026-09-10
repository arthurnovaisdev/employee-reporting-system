package com.mbfreire.employee_reporting.controller;

import com.mbfreire.employee_reporting.dto.request.CategoryRequestDTO;
import com.mbfreire.employee_reporting.dto.response.CategoryResponseDTO;
import com.mbfreire.employee_reporting.service.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> create(@Valid @RequestBody CategoryRequestDTO dto) {
        CategoryResponseDTO response = categoryService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<CategoryResponseDTO>> findAll(@RequestParam(defaultValue = "0") @Min(value = 0, message = "A página não pode ser negativa.") int page, @RequestParam(defaultValue = "20") @Min(value = 1, message = "O tamanho da página deve ser no mínimo 1.") @Max(value = 50, message = "O tamanho da página deve ser no máximo 50.") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        return ResponseEntity.ok(categoryService.findAll(pageable));
    }
}
