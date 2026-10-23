package com.performtrack.employee;

import com.performtrack.common.BusinessRuleException;
import com.performtrack.common.ResourceNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {

        String email = normalizeEmail(request.email());

        if (employeeRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessRuleException(
                    "An employee with this email already exists.");
        }

        Employee employee = new Employee();
        applyRequest(employee, request);

        return toResponse(employeeRepository.save(employee));
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAll() {

        return employeeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getById(Long id) {
        return toResponse(findEmployee(id));
    }

    @Transactional
    public EmployeeResponse update(
            Long id, EmployeeRequest request) {

        Employee employee = findEmployee(id);

        String email = normalizeEmail(request.email());

        if (employeeRepository
                .existsByEmailIgnoreCaseAndIdNot(email, id)) {

            throw new BusinessRuleException(
                    "Another employee already uses this email.");
        }

        applyRequest(employee, request);

        return toResponse(employeeRepository.save(employee));
    }

    @Transactional
    public void deactivate(Long id) {

        Employee employee = findEmployee(id);
        employee.setActive(false);

        employeeRepository.save(employee);
    }

    private void applyRequest(
            Employee employee,
            EmployeeRequest request) {

        employee.setFullName(request.fullName().trim());
        employee.setEmail(normalizeEmail(request.email()));
        employee.setDepartment(cleanOptional(request.department()));
        employee.setDesignation(cleanOptional(request.designation()));
        employee.setJoiningDate(request.joiningDate());

        if (request.managerId() == null) {
            employee.setReportingManager(null);
            return;
        }

        if (employee.getId() != null &&
                request.managerId().equals(employee.getId())) {

            throw new BusinessRuleException(
                    "An employee cannot be their own reporting manager.");
        }

        Employee manager = findEmployee(request.managerId());

        if (!manager.isActive()) {
            throw new BusinessRuleException(
                    "An inactive employee cannot be a reporting manager.");
        }

        employee.setReportingManager(manager);
    }

    private Employee findEmployee(Long id) {

        return employeeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee not found with id: " + id));
    }

    private EmployeeResponse toResponse(Employee employee) {

        Employee manager = employee.getReportingManager();

        return new EmployeeResponse(
                employee.getId(),
                employee.getFullName(),
                employee.getEmail(),
                employee.getDepartment(),
                employee.getDesignation(),
                employee.getJoiningDate(),
                manager == null ? null : manager.getId(),
                manager == null ? null : manager.getFullName(),
                employee.isActive());
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String cleanOptional(String value) {
        return value == null || value.isBlank()
                ? null
                : value.trim();
    }
}
