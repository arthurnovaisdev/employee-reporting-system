package com.mbfreire.employee_reporting.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(
        @NotBlank(message = "O CPF não pode estar vazio.")
        @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos númericos.")
        String cpf,

        @NotBlank(message = "A senha não pode estar vazia.")
        @Size(min = 8, max = 100, message = "A senha deve ter entre 8 e 100 caracteres.")
        String password
) {}
