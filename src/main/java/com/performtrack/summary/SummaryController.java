package com.performtrack.summary;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/summaries")
public class SummaryController {

    private final SummaryService summaryService;

    public SummaryController(SummaryService summaryService) {
        this.summaryService = summaryService;
    }

    @GetMapping("/employees/{employeeId}")
    public EmployeePerformanceSummary getEmployeeSummary(
            @PathVariable Long employeeId) {
        return summaryService.getEmployeeSummary(employeeId);
    }

    @GetMapping("/cycles/{cycleId}")
    public CyclePerformanceSummary getCycleSummary(
            @PathVariable Long cycleId) {
        return summaryService.getCycleSummary(cycleId);
    }
}

