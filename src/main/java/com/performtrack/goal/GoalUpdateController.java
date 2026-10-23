package com.performtrack.goal;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals/{goalId}/updates")
public class GoalUpdateController {

    private final GoalUpdateService goalUpdateService;

    public GoalUpdateController(GoalUpdateService goalUpdateService) {
        this.goalUpdateService = goalUpdateService;
    }

    @PostMapping
    public ResponseEntity<GoalUpdateResponse> addUpdate(
            @PathVariable Long goalId,
            @Valid @RequestBody GoalUpdateRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(goalUpdateService.addUpdate(goalId, request));
    }

    @GetMapping
    public List<GoalUpdateResponse> getUpdates(
            @PathVariable Long goalId) {

        return goalUpdateService.getUpdates(goalId);
    }
}
