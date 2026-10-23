package com.performtrack.goal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findByEmployeeId(Long employeeId);

    List<Goal> findByReviewCycleId(Long reviewCycleId);

    List<Goal> findByEmployeeIdAndReviewCycleId(
            Long employeeId,
            Long reviewCycleId
    );
}
