package com.performtrack.employee;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository
        extends JpaRepository<Employee, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(
            String email, Long id);
}
