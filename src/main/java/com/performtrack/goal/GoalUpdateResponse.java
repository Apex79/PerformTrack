package com.performtrack.goal;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record GoalUpdateResponse(
        Long id,
        Long goalId,
        BigDecimal progressValue,
        String note,
        LocalDateTime updatedAt
) {}
