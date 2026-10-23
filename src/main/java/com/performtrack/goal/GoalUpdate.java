package com.performtrack.goal;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "goal_updates")
public class GoalUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal progressValue;

    @Column(length = 1000)
    private String note;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    private Goal goal;

    public GoalUpdate() {
    }

    @PrePersist
    protected void onCreate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getProgressValue() {
        return progressValue;
    }

    public void setProgressValue(BigDecimal progressValue) {
        this.progressValue = progressValue;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }
}
