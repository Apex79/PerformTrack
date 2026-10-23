package com.performtrack.goal;

import com.performtrack.common.BusinessRuleException;
import com.performtrack.common.ResourceNotFoundException;
import com.performtrack.employee.Employee;
import com.performtrack.employee.EmployeeRepository;
import com.performtrack.reviewcycle.ReviewCycle;
import com.performtrack.reviewcycle.ReviewCycleRepository;
import com.performtrack.security.CurrentUserService;
import com.performtrack.security.Role;
import com.performtrack.security.UserAccount;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class GoalService {

    private final CurrentUserService currentUserService;
    private final GoalRepository goalRepository;
    private final EmployeeRepository employeeRepository;
    private final ReviewCycleRepository reviewCycleRepository;

    public GoalService(
            CurrentUserService currentUserService,
            GoalRepository goalRepository,
            EmployeeRepository employeeRepository,
            ReviewCycleRepository reviewCycleRepository) {
        this.currentUserService = currentUserService;
        this.goalRepository = goalRepository;
        this.employeeRepository = employeeRepository;
        this.reviewCycleRepository = reviewCycleRepository;
    }

    private void checkEmployeeAccess(Employee employee) {
        UserAccount user = currentUserService.getCurrentUser();

        if (user.getRole() == Role.HR) {
            return;
        }

        if (user.getEmployee() == null) {
            throw new AccessDeniedException(
                    "Your account is not linked to an employee");
        }

        Long currentEmployeeId = user.getEmployee().getId();

        if (user.getRole() == Role.EMPLOYEE) {
            if (!currentEmployeeId.equals(employee.getId())) {
                throw new AccessDeniedException(
                        "You can access only your own goals");
            }
            return;
        }

        if (user.getRole() == Role.MANAGER) {
            if (employee.getManagerId() == null
                    || !currentEmployeeId.equals(employee.getManagerId())) {
                throw new AccessDeniedException(
                        "You can access only goals of your assigned employees");
            }
            return;
        }

        throw new AccessDeniedException("Access denied");
    }

    private void checkGoalAccess(Goal goal) {
        checkEmployeeAccess(goal.getEmployee());
    }

    @Transactional
    public GoalResponse create(GoalRequest request) {

        Employee employee = employeeRepository.findById(request.employeeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with id: " + request.employeeId()));

        checkEmployeeAccess(employee);

        if (!employee.isActive()) {
            throw new BusinessRuleException(
                    "Cannot assign a goal to an inactive employee.");
        }

        ReviewCycle cycle = reviewCycleRepository.findById(
                        request.reviewCycleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Review cycle not found with id: "
                                + request.reviewCycleId()));

        if (!cycle.isActive()) {
            throw new BusinessRuleException(
                    "Cannot assign a goal to an inactive review cycle.");
        }

        if (request.dueDate() != null
                && (request.dueDate().isBefore(cycle.getStartDate())
                || request.dueDate().isAfter(cycle.getEndDate()))) {
            throw new BusinessRuleException(
                    "Goal due date must fall within the review cycle.");
        }

        Goal goal = new Goal();
        goal.setTitle(request.title().trim());
        goal.setDescription(cleanOptional(request.description()));
        goal.setTargetValue(request.targetValue());
        goal.setCurrentValue(BigDecimal.ZERO);
        goal.setUnit(request.unit().trim());
        goal.setDueDate(request.dueDate());
        goal.setStatus("IN_PROGRESS");
        goal.setEmployee(employee);
        goal.setReviewCycle(cycle);

        return toResponse(goalRepository.save(goal));
    }

    @Transactional(readOnly = true)
    public List<GoalResponse> getAll() {
        UserAccount user = currentUserService.getCurrentUser();

        if (user.getRole() == Role.HR) {
            return goalRepository.findAll()
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }

        return goalRepository.findAll()
                .stream()
                .filter(goal -> canAccessGoal(user, goal))
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public GoalResponse getById(Long id) {
        Goal goal = findGoal(id);
        checkGoalAccess(goal);
        return toResponse(goal);
    }

    @Transactional(readOnly = true)
    public List<GoalResponse> getByEmployee(Long employeeId) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with id: " + employeeId));

        checkEmployeeAccess(employee);

        return goalRepository.findByEmployeeId(employeeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GoalResponse> getByReviewCycle(Long cycleId) {

        if (!reviewCycleRepository.existsById(cycleId)) {
            throw new ResourceNotFoundException(
                    "Review cycle not found with id: " + cycleId);
        }

        UserAccount user = currentUserService.getCurrentUser();

        return goalRepository.findByReviewCycleId(cycleId)
                .stream()
                .filter(goal -> canAccessGoal(user, goal))
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public GoalResponse update(Long id, GoalRequest request) {

        Goal goal = findGoal(id);
        checkGoalAccess(goal);

        Employee employee = employeeRepository.findById(request.employeeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with id: " + request.employeeId()));

        checkEmployeeAccess(employee);

        ReviewCycle cycle = reviewCycleRepository.findById(
                        request.reviewCycleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Review cycle not found with id: "
                                + request.reviewCycleId()));

        if (!employee.isActive()) {
            throw new BusinessRuleException(
                    "Cannot assign a goal to an inactive employee.");
        }

        if (!cycle.isActive()) {
            throw new BusinessRuleException(
                    "Cannot assign a goal to an inactive review cycle.");
        }

        if (request.dueDate() != null
                && (request.dueDate().isBefore(cycle.getStartDate())
                || request.dueDate().isAfter(cycle.getEndDate()))) {
            throw new BusinessRuleException(
                    "Goal due date must fall within the review cycle.");
        }

        if (goal.getCurrentValue().compareTo(request.targetValue()) > 0) {
            throw new BusinessRuleException(
                    "Target cannot be lower than current progress.");
        }

        goal.setTitle(request.title().trim());
        goal.setDescription(cleanOptional(request.description()));
        goal.setTargetValue(request.targetValue());
        goal.setUnit(request.unit().trim());
        goal.setDueDate(request.dueDate());
        goal.setEmployee(employee);
        goal.setReviewCycle(cycle);

        return toResponse(goalRepository.save(goal));
    }

    @Transactional
    public void delete(Long id) {
        Goal goal = findGoal(id);
        checkGoalAccess(goal);
        goalRepository.delete(goal);
    }

    private boolean canAccessGoal(UserAccount user, Goal goal) {

        if (user.getRole() == Role.HR) {
            return true;
        }

        if (user.getEmployee() == null) {
            return false;
        }

        Long currentEmployeeId = user.getEmployee().getId();

        if (user.getRole() == Role.EMPLOYEE) {
            return currentEmployeeId.equals(goal.getEmployee().getId());
        }

        if (user.getRole() == Role.MANAGER) {
            Long managerId = goal.getEmployee().getManagerId();
            return managerId != null && currentEmployeeId.equals(managerId);
        }

        return false;
    }

    private Goal findGoal(Long id) {
        return goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Goal not found with id: " + id));
    }

    private GoalResponse toResponse(Goal goal) {
        return new GoalResponse(
                goal.getId(),
                goal.getTitle(),
                goal.getDescription(),
                goal.getTargetValue(),
                goal.getCurrentValue(),
                goal.getUnit(),
                goal.getDueDate(),
                goal.getStatus(),
                goal.getEmployee().getId(),
                goal.getReviewCycle().getId()
        );
    }

    private String cleanOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
