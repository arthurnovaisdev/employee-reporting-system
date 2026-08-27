package com.mbfreire.employee_reporting.dto.response;

import java.util.UUID;

public record CategoryResponseDTO(
        UUID id,
        String name,
        boolean active
) {}
