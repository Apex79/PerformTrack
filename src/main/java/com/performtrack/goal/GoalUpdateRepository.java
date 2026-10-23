package com.performtrack.goal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalUpdateRepository
        extends JpaRepository<GoalUpdate, Long> {

    List<GoalUpdate> findByGoalIdOrderByUpdatedAtAsc(Long goalId);
}
