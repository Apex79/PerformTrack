package com.performtrack.reviewcycle;

import java.time.LocalDate;

public record ReviewCycleResponse(
        Long id,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        boolean active
) {}
