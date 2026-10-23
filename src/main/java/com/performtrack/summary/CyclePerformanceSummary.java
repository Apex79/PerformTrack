package com.performtrack.summary;

public record CyclePerformanceSummary(
        Long reviewCycleId,
        String reviewCycleName,
        Long totalReviews,
        Double averageRating,
        Long completedReviews
) {}

