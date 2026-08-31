package com.mbfreire.employee_reporting.dto.response;

import com.mbfreire.employee_reporting.enums.ReportStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReportAdminResponseDTO(
        String protocol,
        String category,
        String description,
        ReportStatus status,
        LocalDate incidentDate,
        String incidentLocation,
        LocalDateTime createdAt
) {}
