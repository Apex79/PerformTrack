package com.performtrack.employee;

import jakarta.validation.Valid;
import com.performtrack.employee.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> create(
            @Valid @RequestBody EmployeeRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(employeeService.create(request));
    }

    @GetMapping
    public List<EmployeeResponse> getAll() {
        return employeeService.getAll();
    }

    @GetMapping("/{id}")
    public EmployeeResponse getById(@PathVariable Long id) {
        return employeeService.getById(id);
    }

    @PutMapping("/{id}")
    public EmployeeResponse update(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request) {

        return employeeService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(
            @PathVariable Long id) {

        employeeService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
