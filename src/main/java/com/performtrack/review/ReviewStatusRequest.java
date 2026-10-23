package com.performtrack.review;

import jakarta.validation.constraints.NotBlank;

public record ReviewStatusRequest(

        @NotBlank(message = "Status is required")
        String status

) {}
