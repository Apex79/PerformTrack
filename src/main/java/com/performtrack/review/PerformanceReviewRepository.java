package com.performtrack.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PerformanceReviewRepository
        extends JpaRepository<PerformanceReview, Long> {

    boolean existsByEmployeeIdAndReviewCycleId(
            Long employeeId, Long reviewCycleId);

    List<PerformanceReview> findByEmployeeId(Long employeeId);

    List<PerformanceReview> findByReviewCycleId(Long reviewCycleId);

    List<PerformanceReview> findByEmployeeIdAndReleasedTrue(
            Long employeeId);

    @Query("""
    SELECT new com.performtrack.summary.EmployeePerformanceSummary(
        e.id,
        e.fullName,
        COUNT(r),
        AVG(r.rating),
        SUM(CASE WHEN r.status = 'COMPLETED' THEN 1 ELSE 0 END)
    )
    FROM PerformanceReview r
    JOIN r.employee e
    WHERE e.id = :employeeId
      AND r.released = true
      AND r.rating IS NOT NULL
    GROUP BY e.id, e.fullName
    """)
    java.util.Optional<com.performtrack.summary.EmployeePerformanceSummary>
    getEmployeeSummary(@Param("employeeId") Long employeeId);


    @Query("""
    SELECT new com.performtrack.summary.CyclePerformanceSummary(
        c.id,
        c.name,
        COUNT(r),
        AVG(r.rating),
        SUM(CASE WHEN r.status = 'COMPLETED' THEN 1 ELSE 0 END)
    )
    FROM PerformanceReview r
    JOIN r.reviewCycle c
    WHERE c.id = :cycleId
      AND r.released = true
      AND r.rating IS NOT NULL
    GROUP BY c.id, c.name
    """)
    java.util.Optional<com.performtrack.summary.CyclePerformanceSummary>
    getCycleSummary(@Param("cycleId") Long cycleId);

}
