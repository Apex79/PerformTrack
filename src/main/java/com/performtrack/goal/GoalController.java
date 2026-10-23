package com.performtrack.goal;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping
    public ResponseEntity<GoalResponse> create(
            @Valid @RequestBody GoalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(goalService.create(request));
    }

    @GetMapping
    public List<GoalResponse> getAll() {
        return goalService.getAll();
    }

    @GetMapping("/{id}")
    public GoalResponse getById(@PathVariable Long id) {
        return goalService.getById(id);
    }

    @GetMapping("/employee/{employeeId}")
    public List<GoalResponse> getByEmployee(
            @PathVariable Long employeeId) {
        return goalService.getByEmployee(employeeId);
    }

    @GetMapping("/cycle/{cycleId}")
    public List<GoalResponse> getByReviewCycle(
            @PathVariable Long cycleId) {
        return goalService.getByReviewCycle(cycleId);
    }

    @PutMapping("/{id}")
    public GoalResponse update(
            @PathVariable Long id,
            @Valid @RequestBody GoalRequest request) {
        return goalService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        goalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
