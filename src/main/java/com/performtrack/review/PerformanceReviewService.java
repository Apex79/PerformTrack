package com.performtrack.review;

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

import java.util.List;

@Service
public class PerformanceReviewService {

    private final PerformanceReviewRepository reviewRepository;
    private final EmployeeRepository employeeRepository;
    private final ReviewCycleRepository cycleRepository;
    private final CurrentUserService currentUserService;

    public PerformanceReviewService(
            PerformanceReviewRepository reviewRepository,
            EmployeeRepository employeeRepository,
            ReviewCycleRepository cycleRepository,
            CurrentUserService currentUserService) {

        this.reviewRepository = reviewRepository;
        this.employeeRepository = employeeRepository;
        this.cycleRepository = cycleRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public PerformanceReviewResponse create(
            PerformanceReviewRequest request) {

        UserAccount user = currentUserService.getCurrentUser();

        // Only HR and managers can create reviews.
        if (user.getRole() == Role.EMPLOYEE) {
            throw new AccessDeniedException(
                    "Employees cannot create performance reviews.");
        }

        if (request.employeeId().equals(request.managerId())) {
            throw new BusinessRuleException(
                    "An employee cannot review themselves.");
        }

        if (reviewRepository.existsByEmployeeIdAndReviewCycleId(
                request.employeeId(), request.reviewCycleId())) {
            throw new BusinessRuleException(
                    "A review already exists for this employee and cycle.");
        }

        Employee employee = findEmployee(request.employeeId());
        Employee manager = findEmployee(request.managerId());
        ReviewCycle cycle = findCycle(request.reviewCycleId());

        if (!employee.isActive() || !manager.isActive()) {
            throw new BusinessRuleException(
                    "Employee and manager must both be active.");
        }

        if (!cycle.isActive()) {
            throw new BusinessRuleException(
                    "Cannot create a review for an inactive cycle.");
        }

        if (employee.getReportingManager() == null
                || !employee.getReportingManager().getId()
                .equals(manager.getId())) {
            throw new BusinessRuleException(
                    "The selected manager is not assigned to this employee.");
        }

        // A manager can create reviews only for their assigned employees.
        if (user.getRole() == Role.MANAGER) {
            checkManagerAccess(employee, user);
            if (!manager.getId().equals(user.getEmployee().getId())) {
                throw new AccessDeniedException(
                        "You can create reviews only as the assigned manager.");
            }
        }

        PerformanceReview review = new PerformanceReview();
        review.setEmployee(employee);
        review.setManager(manager);
        review.setReviewCycle(cycle);
        review.setStatus("DRAFT");
        review.setReleased(false);

        return toResponse(reviewRepository.save(review));
    }

    @Transactional(readOnly = true)
    public List<PerformanceReviewResponse> getAll() {

        UserAccount user = currentUserService.getCurrentUser();

        return reviewRepository.findAll()
                .stream()
                .filter(review -> canAccessReview(review, user))
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PerformanceReviewResponse getById(Long id) {

        PerformanceReview review = findReview(id);
        checkReviewAccess(review);

        return toResponse(review);
    }

    @Transactional(readOnly = true)
    public List<PerformanceReviewResponse> getByEmployee(
            Long employeeId) {

        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException(
                    "Employee not found with id: " + employeeId);
        }

        UserAccount user = currentUserService.getCurrentUser();

        // Employees can view only their own reviews.
        if (user.getRole() == Role.EMPLOYEE
                && !employeeId.equals(user.getEmployee().getId())) {
            throw new AccessDeniedException(
                    "You can view only your own reviews.");
        }

        // Managers can view reviews only for assigned employees.
        if (user.getRole() == Role.MANAGER) {
            Employee employee = findEmployee(employeeId);
            checkManagerAccess(employee, user);
        }

        return reviewRepository.findByEmployeeId(employeeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PerformanceReviewResponse> getReleasedByEmployee(
            Long employeeId) {

        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException(
                    "Employee not found with id: " + employeeId);
        }

        UserAccount user = currentUserService.getCurrentUser();

        if (user.getRole() == Role.EMPLOYEE
                && !employeeId.equals(user.getEmployee().getId())) {
            throw new AccessDeniedException(
                    "You can view only your own released reviews.");
        }

        if (user.getRole() == Role.MANAGER) {
            checkManagerAccess(findEmployee(employeeId), user);
        }

        return reviewRepository.findByEmployeeIdAndReleasedTrue(employeeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public PerformanceReviewResponse assess(
            Long id, ReviewAssessmentRequest request) {

        PerformanceReview review = findReview(id);

        checkReviewAccess(review);
        checkManagerOrHr();

        if (!"DRAFT".equals(review.getStatus())) {
            throw new BusinessRuleException(
                    "Only draft reviews can be edited.");
        }

        review.setRating(request.rating());

        String feedback = request.feedback();
        review.setFeedback(
                feedback == null || feedback.isBlank()
                        ? null : feedback.trim());

        return toResponse(reviewRepository.save(review));
    }

    @Transactional
    public PerformanceReviewResponse changeStatus(
            Long id, ReviewStatusRequest request) {

        PerformanceReview review = findReview(id);

        checkReviewAccess(review);
        checkManagerOrHr();

        String currentStatus = review.getStatus();
        String newStatus = request.status().trim().toUpperCase();

        boolean validTransition =
                ("DRAFT".equals(currentStatus)
                        && "SUBMITTED".equals(newStatus))
                        || ("SUBMITTED".equals(currentStatus)
                        && "COMPLETED".equals(newStatus));

        if (!validTransition) {
            throw new BusinessRuleException(
                    "Invalid status transition. Allowed: DRAFT -> SUBMITTED -> COMPLETED.");
        }

        if ("SUBMITTED".equals(newStatus)
                && (review.getRating() == null
                || review.getFeedback() == null
                || review.getFeedback().isBlank())) {
            throw new BusinessRuleException(
                    "Rating and feedback are required before submission.");
        }

        review.setStatus(newStatus);
        return toResponse(reviewRepository.save(review));
    }

    @Transactional
    public PerformanceReviewResponse release(
            Long id, ReviewReleaseRequest request) {

        UserAccount user = currentUserService.getCurrentUser();

        if (user.getRole() != Role.HR) {
            throw new AccessDeniedException(
                    "Only HR can release performance reviews.");
        }

        PerformanceReview review = findReview(id);

        if (request.released()
                && !"COMPLETED".equals(review.getStatus())) {
            throw new BusinessRuleException(
                    "Only completed reviews can be released.");
        }

        review.setReleased(request.released());
        return toResponse(reviewRepository.save(review));
    }

    private void checkManagerOrHr() {

        Role role = currentUserService.getCurrentUser().getRole();

        if (role != Role.MANAGER && role != Role.HR) {
            throw new AccessDeniedException(
                    "Only managers and HR can modify reviews.");
        }
    }

    private void checkReviewAccess(PerformanceReview review) {
        UserAccount user = currentUserService.getCurrentUser();

        if (!canAccessReview(review, user)) {
            throw new AccessDeniedException(
                    "You are not allowed to access this review.");
        }
    }

    private boolean canAccessReview(
            PerformanceReview review, UserAccount user) {

        if (user.getRole() == Role.HR) {
            return true;
        }

        if (user.getRole() == Role.EMPLOYEE) {
            return review.getEmployee().getId()
                    .equals(user.getEmployee().getId());
        }

        if (user.getRole() == Role.MANAGER) {
            return review.getManager().getId()
                    .equals(user.getEmployee().getId());
        }

        return false;
    }

    private void checkManagerAccess(
            Employee employee, UserAccount user) {

        if (employee.getReportingManager() == null
                || !employee.getReportingManager().getId()
                .equals(user.getEmployee().getId())) {
            throw new AccessDeniedException(
                    "You can access reviews only for your assigned employees.");
        }
    }

    private Employee findEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with id: " + id));
    }

    private ReviewCycle findCycle(Long id) {
        return cycleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Review cycle not found with id: " + id));
    }

    private PerformanceReview findReview(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Performance review not found with id: " + id));
    }

    private PerformanceReviewResponse toResponse(
            PerformanceReview review) {

        return new PerformanceReviewResponse(
                review.getId(),
                review.getEmployee().getId(),
                review.getEmployee().getFullName(),
                review.getManager().getId(),
                review.getManager().getFullName(),
                review.getReviewCycle().getId(),
                review.getRating(),
                review.getFeedback(),
                review.getStatus(),
                review.isReleased()
        );
    }
}
