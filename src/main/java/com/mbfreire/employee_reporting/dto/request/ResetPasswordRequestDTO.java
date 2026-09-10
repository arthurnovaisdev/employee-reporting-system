package com.mbfreire.employee_reporting.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequestDTO(
        @NotBlank(message = "O token é obrigatório.")
        @Size(min = 43, max = 43, message = "O token de recuperação é inválido.")
        @Pattern(regexp = "[A-Za-z0-9_-]+", message = "O token de recuperação é inválido.")
        String token,

        @NotBlank(message = "A nova senha não pode estar vazia.")
        @Size(min = 6, max = 100, message = "A senha deve conter entre 6 e 100 caracteres.")
        String newPassword
) {
}
