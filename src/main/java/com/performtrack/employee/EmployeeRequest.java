package com.performtrack.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record EmployeeRequest(

        @NotBlank(message = "Full name is required")
        @Size(max = 100)
        String fullName,

        @NotBlank(message = "Email is required")
        @Email(message = "Enter a valid email address")
        @Size(max = 150)
        String email,

        @Size(max = 80)
        String department,

        @Size(max = 80)
        String designation,

        LocalDate joiningDate,

        Long managerId

) {
}
