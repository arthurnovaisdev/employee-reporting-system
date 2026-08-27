package com.mbfreire.employee_reporting.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record ReportRequestDTO(
        @NotNull(message = "A categoria da denúncia é obrigatória.")
        UUID categoryId,

        @NotBlank(message = "A descrição da denúncia não pode estar vazia.")
        @Size(max = 5000, message = "A descrição deve ter no máximo 5000 caracteres.")
        String description,

        LocalDate incidentDate,
        String incidentLocation
) {}
