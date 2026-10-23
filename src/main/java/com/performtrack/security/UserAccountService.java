package com.performtrack.security;

import com.performtrack.employee.Employee;
import com.performtrack.employee.EmployeeRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAccountService {

    private final UserAccountRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAccountService(
            UserAccountRepository userRepository,
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserAccountResponse createUser(CreateUserRequest request) {

        String username = request.username().trim();

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        UserAccount user = new UserAccount();
        user.setUsername(username);
        user.setPasswordHash(
                passwordEncoder.encode(request.password())
        );
        user.setRole(request.role());

        if (request.role() == Role.HR) {
            if (request.employeeId() != null) {
                throw new IllegalArgumentException(
                        "HR account must not have an employee ID"
                );
            }
        } else {
            if (request.employeeId() == null) {
                throw new IllegalArgumentException(
                        "Employee ID is required for this role"
                );
            }

            Employee employee = employeeRepository
                    .findById(request.employeeId())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Employee not found"
                            ));

            if (!employee.isActive()) {
                throw new IllegalArgumentException(
                        "Cannot create an account for an inactive employee"
                );
            }

            if (userRepository.existsByEmployeeId(employee.getId())) {
                throw new IllegalArgumentException(
                        "This employee already has a login account"
                );
            }

            user.setEmployee(employee);
        }

        UserAccount saved = userRepository.save(user);

        return new UserAccountResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getRole(),
                saved.getEmployee() == null
                        ? null
                        : saved.getEmployee().getId()
        );
    }
}
