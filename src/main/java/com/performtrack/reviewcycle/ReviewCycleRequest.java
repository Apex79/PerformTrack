package com.performtrack.reviewcycle;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ReviewCycleRequest(

        @NotBlank(message = "Review cycle name is required")
        @Size(max = 100, message = "Name cannot exceed 100 characters")
        String name,

        @NotNull(message = "Start date is required")
        LocalDate startDate,

        @NotNull(message = "End date is required")
        LocalDate endDate

) {}
