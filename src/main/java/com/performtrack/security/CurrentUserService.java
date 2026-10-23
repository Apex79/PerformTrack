package com.performtrack.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UserAccountRepository userRepository;

    public CurrentUserService(UserAccountRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserAccount getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication.getName().equals("anonymousUser")) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Authentication required");
        }

        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() ->
                        new org.springframework.security.access.AccessDeniedException(
                                "User account not found"));
    }

    public Long getCurrentEmployeeId() {
        UserAccount user = getCurrentUser();

        if (user.getEmployee() == null) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "This account is not linked to an employee");
        }

        return user.getEmployee().getId();
    }

    public boolean hasRole(Role role) {
        return getCurrentUser().getRole() == role;
    }
}
