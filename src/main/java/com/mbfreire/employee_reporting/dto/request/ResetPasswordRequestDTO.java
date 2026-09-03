package com.mbfreire.employee_reporting.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequestDTO(
        @NotBlank(message = "O token é obrigatório.")
        String token,

        @NotBlank(message = "A nova senha não pode estar vazia.")
        @Size(min = 8, max = 100, message = "A senha deve conter entre 8 e 100 caracteres.")
        String newPassword
) {
}
