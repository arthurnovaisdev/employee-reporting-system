package com.mbfreire.employee_reporting.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequestDTO(
   @NotBlank(message = "A senha atual não pode estar vazia.")
   @Size(max = 100, message = "A senha atual deve possuir no máximo 100 caracteres.")
   String currentPassword,

   @NotBlank(message = "A nova senha não pode estar vazia.")
   @Size(min = 6, max = 100, message = "A nova senha deve ter entre 6 e 100 caracteres.")
   String newPassword
) {}
