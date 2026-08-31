package com.mbfreire.employee_reporting.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(
        @NotBlank(message = "O nome não pode estar vazio.")
        @Size(max = 150)
        String name,

        @NotBlank(message = "O e-mail não pode estar vazio.")
        @Email(message = "Informe um e-mail válido.")
        @Size(max = 150)
        @Pattern(regexp = "^.+@empresa\\.com$", message = "Use o e-mail corporativo da empresa.")
        String email,

        @NotBlank(message = "A senha não pode estar vazia.")
        @Size(min = 8, max = 100, message = "A senha deve ter entre 8 e 10 caracteres.")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>._]).*$",
                message = "A senha deve conter pelo menos uma letra maiúscula e um caractere especial."
        )
        String password
) {}
