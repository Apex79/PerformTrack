package com.performtrack.security;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository
        extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmployeeId(Long employeeId);
}
