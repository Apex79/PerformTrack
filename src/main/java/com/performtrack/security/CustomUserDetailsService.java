package com.performtrack.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UserAccountRepository userRepository;

    public CustomUserDetailsService(
            UserAccountRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        UserAccount account = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found"
                        ));

        return User.withUsername(account.getUsername())
                .password(account.getPasswordHash())
                .roles(account.getRole().name())
                .build();
    }
}
