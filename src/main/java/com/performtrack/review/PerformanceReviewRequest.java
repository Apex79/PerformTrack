package com.performtrack.review;

import jakarta.validation.constraints.NotNull;

public record PerformanceReviewRequest(

        @NotNull(message = "Employee ID is required")
        Long employeeId,

        @NotNull(message = "Manager ID is required")
        Long managerId,

        @NotNull(message = "Review cycle ID is required")
        Long reviewCycleId

) {}
