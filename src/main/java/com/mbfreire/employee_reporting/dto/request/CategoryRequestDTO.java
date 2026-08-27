package com.mbfreire.employee_reporting.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequestDTO(
        @NotBlank(message = "O nome da categoria é obrigatório.")
        @Size(max = 100, message = "O nome da categoria deve ter no máximo 100 caracteres.")
        String name,

        boolean active
) {}
