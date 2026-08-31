package com.mbfreire.employee_reporting.dto.request;

import com.mbfreire.employee_reporting.enums.ReportStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReportStatusUpdateRequestDTO(
        @NotNull(message = "O novo status é obrigatório.")
        ReportStatus newStatus,

        @Size(max = 2000, message = "A observação deve ter no máximo 2000 caracteres.")
        String note
) {}
