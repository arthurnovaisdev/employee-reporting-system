package com.mbfreire.employee_reporting.dto.response;

import com.mbfreire.employee_reporting.enums.ReportStatus;

import java.time.LocalDateTime;

public record ReportResponseDTO(
        String protocol,
        String category,
        String description,
        ReportStatus status,
        LocalDateTime createdAt
) {}
