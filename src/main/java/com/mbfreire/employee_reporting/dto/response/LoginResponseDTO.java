package com.mbfreire.employee_reporting.dto.response;

public record LoginResponseDTO(
        String token,
        String name,
        String role,
        boolean passwordChanged
) {}
