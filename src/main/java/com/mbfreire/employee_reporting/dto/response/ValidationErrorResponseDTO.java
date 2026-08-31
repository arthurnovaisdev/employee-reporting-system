package com.mbfreire.employee_reporting.dto.response;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationErrorResponseDTO(
        int status,
        Map<String, String> detalhes,
        LocalDateTime timestamp
) {}
