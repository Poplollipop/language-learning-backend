package com.languagelearning.controller;

import com.languagelearning.dto.AuthResponse;
import com.languagelearning.dto.LoginRequest;
import com.languagelearning.dto.RegisterRequest;
import com.languagelearning.model.User;
import com.languagelearning.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(authService.register(request)));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return new AuthResponse(authService.login(request));
    }

    @GetMapping("/me")
    public Map<String, String> me(@AuthenticationPrincipal User user) {
        return Map.of("email", user.getEmail(), "role", user.getRole().name());
    }
}
