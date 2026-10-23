package com.performtrack.review;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class PerformanceReviewController {

    private final PerformanceReviewService reviewService;

    public PerformanceReviewController(
            PerformanceReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<PerformanceReviewResponse> create(
            @Valid @RequestBody PerformanceReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.create(request));
    }

    @GetMapping
    public List<PerformanceReviewResponse> getAll() {
        return reviewService.getAll();
    }

    @GetMapping("/{id}")
    public PerformanceReviewResponse getById(@PathVariable Long id) {
        return reviewService.getById(id);
    }

    @GetMapping("/employee/{employeeId}")
    public List<PerformanceReviewResponse> getByEmployee(
            @PathVariable Long employeeId) {
        return reviewService.getByEmployee(employeeId);
    }

    @GetMapping("/employee/{employeeId}/released")
    public List<PerformanceReviewResponse> getReleasedByEmployee(
            @PathVariable Long employeeId) {
        return reviewService.getReleasedByEmployee(employeeId);
    }

    @PutMapping("/{id}/assessment")
    public PerformanceReviewResponse assess(
            @PathVariable Long id,
            @Valid @RequestBody ReviewAssessmentRequest request) {
        return reviewService.assess(id, request);
    }

    @PatchMapping("/{id}/status")
    public PerformanceReviewResponse changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody ReviewStatusRequest request) {
        return reviewService.changeStatus(id, request);
    }

    @PatchMapping("/{id}/release")
    public PerformanceReviewResponse release(
            @PathVariable Long id,
            @RequestBody ReviewReleaseRequest request) {
        return reviewService.release(id, request);
    }
}
