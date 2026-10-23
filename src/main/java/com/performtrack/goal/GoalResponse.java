package com.performtrack.goal;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GoalResponse(
        Long id,
        String title,
        String description,
        BigDecimal targetValue,
        BigDecimal currentValue,
        String unit,
        LocalDate dueDate,
        String status,
        Long employeeId,
        Long reviewCycleId
) {}
