package com.performtrack.summary;

public record EmployeePerformanceSummary(
        Long employeeId,
        String employeeName,
        Long totalReviews,
        Double averageRating,
        Long completedReviews
) {}
