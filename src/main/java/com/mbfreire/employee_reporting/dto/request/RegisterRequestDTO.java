package com.mbfreire.employee_reporting.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(
        @NotBlank(message = "O nome não pode estar vazio.")
        @Size(max = 150, message = "O nome deve possuir no máximo 150 caracteres.")
        String name,

        @NotBlank(message = "O CPF não pode estar vazio.")
        @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos numéricos.")
        String cpf,

        @Email(message = "Informe um e-mail de contato válido.")
        @Size(max = 150, message = "O e-mail deve possuir no máximo 150 caracteres.")
        String contactEmail,

        @NotBlank(message = "A senha provisória não pode estar vazia.")
        @Size(min = 6, max = 100, message = "A senha deve ter entre 6 e 100 caracteres.")
        String password
) {}
