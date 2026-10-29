package com.performtrack.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/accounts"
                        ).hasRole("HR")
                        // HR manages employee records.
                        .requestMatchers(
                                "/api/employees/**"
                        ).hasRole("HR")

                        // HR manages review cycles.
                        .requestMatchers(
                                "/api/review-cycles/**"
                        ).hasRole("HR")

                        // Goals: authenticated users for now.
                        .requestMatchers(
                                "/api/goals/**"
                        ).hasAnyRole("EMPLOYEE", "MANAGER", "HR")

                        // Reviews: authenticated users for now.
                        .requestMatchers(
                                "/api/reviews/**"
                        ).hasAnyRole("EMPLOYEE", "MANAGER", "HR")

                        // Summaries: authenticated users for now.
                        .requestMatchers(
                                "/api/summaries/**"
                        ).hasAnyRole("EMPLOYEE", "MANAGER", "HR")

                        .anyRequest().authenticated()
                )

                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
