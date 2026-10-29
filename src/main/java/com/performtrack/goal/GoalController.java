package com.performtrack.goal;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    // Only managers can create goals
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<GoalResponse> create(
            @Valid @RequestBody GoalRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(goalService.create(request));
    }

    // View all goals - access must be restricted in the service
    @GetMapping
    public List<GoalResponse> getAll() {
        return goalService.getAll();
    }

    // View a specific goal
    @GetMapping("/{id}")
    public GoalResponse getById(@PathVariable Long id) {
        return goalService.getById(id);
    }

    // View goals belonging to an employee
    @GetMapping("/employee/{employeeId}")
    public List<GoalResponse> getByEmployee(
            @PathVariable Long employeeId) {
        return goalService.getByEmployee(employeeId);
    }

    // View goals belonging to a review cycle
    @GetMapping("/cycle/{cycleId}")
    public List<GoalResponse> getByReviewCycle(
            @PathVariable Long cycleId) {
        return goalService.getByReviewCycle(cycleId);
    }

    // Only managers can edit goals
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public GoalResponse update(
            @PathVariable Long id,
            @Valid @RequestBody GoalRequest request) {

        return goalService.update(id, request);
    }

    // Only managers can delete goals
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        goalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
