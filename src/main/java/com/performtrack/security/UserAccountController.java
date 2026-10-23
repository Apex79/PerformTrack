package com.performtrack.security;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserAccountController {

    private final UserAccountService userAccountService;

    public UserAccountController(
            UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @PostMapping("/accounts")
    @ResponseStatus(HttpStatus.CREATED)
    public UserAccountResponse createAccount(
            @Valid @RequestBody CreateUserRequest request) {
        return userAccountService.createUser(request);
    }
}

