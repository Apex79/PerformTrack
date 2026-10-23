package com.performtrack.review;

public record PerformanceReviewResponse(
        Long id,
        Long employeeId,
        String employeeName,
        Long managerId,
        String managerName,
        Long reviewCycleId,
        Integer rating,
        String feedback,
        String status,
        boolean released
) {}
