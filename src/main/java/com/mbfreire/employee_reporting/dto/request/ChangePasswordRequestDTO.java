package com.mbfreire.employee_reporting.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequestDTO(
   @NotBlank(message = "A senha atual não pode estar vazia.")
   String currentPassword,

   @NotBlank(message = "A nova senha não pode estar vazia.")
   @Size(min = 8, max = 100, message = "A nova senha deve ter entre 8 e 100 caracteres.")
   String newPassword
) {}
