package com.performtrack.security;

public record UserAccountResponse(
        Long id,
        String username,
        Role role,
        Long employeeId
) {}
