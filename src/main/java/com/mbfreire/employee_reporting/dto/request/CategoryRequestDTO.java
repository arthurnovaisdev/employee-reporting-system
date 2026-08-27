package com.mbfreire.employee_reporting.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequestDTO(
        @NotBlank(message = "O nome da categoria é obrigatório.")
        String name,

        boolean active
) {}
