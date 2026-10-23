package com.performtrack.goal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record GoalUpdateRequest(

        @NotNull(message = "Progress value is required")
        @DecimalMin(value = "0.00",
                message = "Progress cannot be negative")
        BigDecimal progressValue,

        @Size(max = 1000)
        String note

) {}
