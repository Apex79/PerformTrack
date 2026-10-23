package com.performtrack.reviewcycle;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewCycleRepository
        extends JpaRepository<ReviewCycle, Long> {

    boolean existsByNameIgnoreCase(String name);
}
