package com.mbfreire.employee_reporting.dto.response;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
   int status,
   String erro,
   LocalDateTime timestamp
) {}
