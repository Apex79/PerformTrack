package com.performtrack.employee;

import java.time.LocalDate;

public record EmployeeResponse(
        Long id,
        String fullName,
        String email,
        String department,
        String designation,
        LocalDate joiningDate,
        Long managerId,
        String managerName,
        boolean active
) {
}
