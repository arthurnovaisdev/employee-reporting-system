package com.mbfreire.employee_reporting.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record ReportRequestDTO(
        @NotNull(message = "A categoria da denúncia é obrigatória.")
        UUID categoryId,

        @NotBlank(message = "A descrição da denúncia não pode estar vazia.")
        String description,

        LocalDate incidentDate,
        String incidentLocation
) {}
