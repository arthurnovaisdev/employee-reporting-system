package com.mbfreire.employee_reporting.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record AttachmentResponseDTO(
        UUID id,
        String originalFileName,
        String contentType,
        Long fileSize,
        LocalDateTime createdAt
) {
}
