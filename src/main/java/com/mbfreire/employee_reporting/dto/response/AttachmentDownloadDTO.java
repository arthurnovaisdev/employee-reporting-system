package com.mbfreire.employee_reporting.dto.response;

import org.springframework.core.io.Resource;

public record AttachmentDownloadDTO(
        Resource resource,
        String originalFileName,
        String contentType
) {
}
