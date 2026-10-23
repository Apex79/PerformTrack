package com.performtrack.goal;

import com.performtrack.common.BusinessRuleException;
import com.performtrack.common.ResourceNotFoundException;
import com.performtrack.security.CurrentUserService;
import com.performtrack.security.Role;
import com.performtrack.security.UserAccount;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class GoalUpdateService {

    private final GoalRepository goalRepository;
    private final GoalUpdateRepository goalUpdateRepository;
    private final CurrentUserService currentUserService;

    public GoalUpdateService(
            GoalRepository goalRepository,
            GoalUpdateRepository goalUpdateRepository,
            CurrentUserService currentUserService) {

        this.goalRepository = goalRepository;
        this.goalUpdateRepository = goalUpdateRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public GoalUpdateResponse addUpdate(
            Long goalId, GoalUpdateRequest request) {

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Goal not found with id: " + goalId));

        checkGoalAccess(goal);

        BigDecimal progress = request.progressValue();

        if (progress.compareTo(goal.getCurrentValue()) < 0) {
            throw new BusinessRuleException(
                    "Progress cannot be lower than the previous value.");
        }

        if (progress.compareTo(goal.getTargetValue()) > 0) {
            throw new BusinessRuleException(
                    "Progress cannot exceed the goal target.");
        }

        if ("COMPLETED".equals(goal.getStatus())) {
            throw new BusinessRuleException(
                    "A completed goal cannot receive further updates.");
        }

        GoalUpdate update = new GoalUpdate();
        update.setProgressValue(progress);
        update.setNote(cleanOptional(request.note()));
        update.setGoal(goal);

        GoalUpdate saved = goalUpdateRepository.save(update);

        goal.setCurrentValue(progress);

        if (progress.compareTo(goal.getTargetValue()) == 0) {
            goal.setStatus("COMPLETED");
        } else {
            goal.setStatus("IN_PROGRESS");
        }

        goalRepository.save(goal);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<GoalUpdateResponse> getUpdates(Long goalId) {

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Goal not found with id: " + goalId));

        checkGoalAccess(goal);

        return goalUpdateRepository
                .findByGoalIdOrderByUpdatedAtAsc(goalId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private void checkGoalAccess(Goal goal) {

        UserAccount user = currentUserService.getCurrentUser();

        // HR can access every goal.
        if (user.getRole() == Role.HR) {
            return;
        }

        Long employeeId = goal.getEmployee().getId();

        // Employees can access only their own goals.
        if (user.getRole() == Role.EMPLOYEE
                && employeeId.equals(user.getEmployee().getId())) {
            return;
        }

        // Managers can access goals of their assigned employees.
        if (user.getRole() == Role.MANAGER
                && goal.getEmployee().getManagerId() != null
                && goal.getEmployee().getManagerId()
                .equals(user.getEmployee().getId())) {
            return;
        }

        throw new AccessDeniedException(
                "You are not allowed to access this goal.");
    }

    private GoalUpdateResponse toResponse(GoalUpdate update) {
        return new GoalUpdateResponse(
                update.getId(),
                update.getGoal().getId(),
                update.getProgressValue(),
                update.getNote(),
                update.getUpdatedAt()
        );
    }

    private String cleanOptional(String value) {
        return value == null || value.isBlank()
                ? null : value.trim();
    }
}
