package com.mbfreire.employee_reporting.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ForgotPasswordRequestDTO(
        @NotBlank(message = "O CPF não pode estar vazio.")
        @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos numéricos.")
        String cpf
) {
}
