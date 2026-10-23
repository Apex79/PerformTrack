package com.performtrack.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewAssessmentRequest(

        @NotNull(message = "Rating is required")
        @Min(value = 1, message = "Rating must be between 1 and 5")
        @Max(value = 5, message = "Rating must be between 1 and 5")
        Integer rating,

        @Size(max = 2000,
                message = "Feedback cannot exceed 2000 characters")
        String feedback

) {}
