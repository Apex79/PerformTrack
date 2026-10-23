package com.performtrack.summary;

import com.performtrack.common.ResourceNotFoundException;
import com.performtrack.employee.Employee;
import com.performtrack.employee.EmployeeRepository;
import com.performtrack.review.PerformanceReviewRepository;
import com.performtrack.reviewcycle.ReviewCycleRepository;
import com.performtrack.security.CurrentUserService;
import com.performtrack.security.Role;
import com.performtrack.security.UserAccount;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SummaryService {

    private final PerformanceReviewRepository reviewRepository;
    private final EmployeeRepository employeeRepository;
    private final ReviewCycleRepository cycleRepository;
    private final CurrentUserService currentUserService;

    public SummaryService(
            PerformanceReviewRepository reviewRepository,
            EmployeeRepository employeeRepository,
            ReviewCycleRepository cycleRepository,
            CurrentUserService currentUserService) {

        this.reviewRepository = reviewRepository;
        this.employeeRepository = employeeRepository;
        this.cycleRepository = cycleRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public EmployeePerformanceSummary getEmployeeSummary(Long employeeId) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with id: " + employeeId));

        checkEmployeeAccess(employee);

        return reviewRepository.getEmployeeSummary(employeeId)
                .orElseGet(() -> new EmployeePerformanceSummary(
                        employeeId, "", 0L, null, 0L));
    }

    @Transactional(readOnly = true)
    public CyclePerformanceSummary getCycleSummary(Long cycleId) {

        if (!cycleRepository.existsById(cycleId)) {
            throw new ResourceNotFoundException(
                    "Review cycle not found with id: " + cycleId);
        }

        UserAccount user = currentUserService.getCurrentUser();

        // Only HR can view an entire cycle's summary.
        if (user.getRole() != Role.HR) {
            throw new AccessDeniedException(
                    "Only HR can view cycle-wide summaries.");
        }

        return reviewRepository.getCycleSummary(cycleId)
                .orElseGet(() -> new CyclePerformanceSummary(
                        cycleId, "", 0L, null, 0L));
    }

    private void checkEmployeeAccess(Employee employee) {

        UserAccount user = currentUserService.getCurrentUser();

        // HR can view summaries for all employees.
        if (user.getRole() == Role.HR) {
            return;
        }

        // Employees can view only their own summaries.
        if (user.getRole() == Role.EMPLOYEE
                && employee.getId().equals(user.getEmployee().getId())) {
            return;
        }

        // Managers can view summaries for their assigned employees.
        if (user.getRole() == Role.MANAGER
                && employee.getReportingManager() != null
                && employee.getReportingManager().getId()
                .equals(user.getEmployee().getId())) {
            return;
        }

        throw new AccessDeniedException(
                "You are not allowed to view this employee's summary.");
    }
}
