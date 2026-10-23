package com.performtrack.reviewcycle;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review-cycles")
public class ReviewCycleController {

    private final ReviewCycleService reviewCycleService;

    public ReviewCycleController(ReviewCycleService reviewCycleService) {
        this.reviewCycleService = reviewCycleService;
    }

    @PostMapping
    public ResponseEntity<ReviewCycleResponse> create(
            @Valid @RequestBody ReviewCycleRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewCycleService.create(request));
    }

    @GetMapping
    public List<ReviewCycleResponse> getAll() {
        return reviewCycleService.getAll();
    }

    @GetMapping("/{id}")
    public ReviewCycleResponse getById(@PathVariable Long id) {
        return reviewCycleService.getById(id);
    }

    @PutMapping("/{id}")
    public ReviewCycleResponse update(
            @PathVariable Long id,
            @Valid @RequestBody ReviewCycleRequest request) {

        return reviewCycleService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        reviewCycleService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}

