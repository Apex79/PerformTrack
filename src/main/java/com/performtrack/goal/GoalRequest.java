package com.performtrack.goal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GoalRequest(

        @NotBlank(message = "Goal title is required")
        @Size(max = 150)
        String title,

        @Size(max = 1000)
        String description,

        @NotNull(message = "Target value is required")
        @DecimalMin(value = "0.01",
                message = "Target value must be greater than zero")
        BigDecimal targetValue,

        @NotBlank(message = "Unit is required")
        @Size(max = 50)
        String unit,

        LocalDate dueDate,

        @NotNull(message = "Employee ID is required")
        Long employeeId,

        @NotNull(message = "Review cycle ID is required")
        Long reviewCycleId

) {}
