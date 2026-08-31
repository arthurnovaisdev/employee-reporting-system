package com.mbfreire.employee_reporting.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(
        @NotBlank(message = "O e-mail não pode estar vazio.")
        @Email(message = "Informe um e-mail válido.")
        @Size(max = 150)
        String email,

        @NotBlank(message = "A senha não pode estar vazia.")
        @Size(min = 8, max = 100, message = "A senha deve ter entre 8 e 100 caracteres.")
        String password
) {}
