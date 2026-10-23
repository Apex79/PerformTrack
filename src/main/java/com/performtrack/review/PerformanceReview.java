package com.performtrack.review;

import com.performtrack.employee.Employee;
import com.performtrack.reviewcycle.ReviewCycle;
import jakarta.persistence.*;

@Entity
@Table(
        name = "performance_reviews",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_review_employee_cycle",
                columnNames = {"employee_id", "review_cycle_id"}
        )
)
public class PerformanceReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private Employee manager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_cycle_id", nullable = false)
    private ReviewCycle reviewCycle;

    private Integer rating;

    @Column(length = 2000)
    private String feedback;

    @Column(nullable = false, length = 20)
    private String status = "DRAFT";

    @Column(nullable = false)
    private boolean released = false;

    public PerformanceReview() {
    }

    public Long getId() {
        return id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Employee getManager() {
        return manager;
    }

    public void setManager(Employee manager) {
        this.manager = manager;
    }

    public ReviewCycle getReviewCycle() {
        return reviewCycle;
    }

    public void setReviewCycle(ReviewCycle reviewCycle) {
        this.reviewCycle = reviewCycle;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isReleased() {
        return released;
    }

    public void setReleased(boolean released) {
        this.released = released;
    }
}
