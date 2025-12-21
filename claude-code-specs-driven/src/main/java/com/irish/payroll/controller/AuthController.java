package com.irish.payroll.controller;

import com.irish.payroll.dto.request.LoginRequest;
import com.irish.payroll.dto.response.JwtResponse;
import com.irish.payroll.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {

    @Autowired
    private AuthenticationService authService;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate and receive JWT token")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        JwtResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Register", description = "Register a new user (for testing)")
    public ResponseEntity<?> register(@Valid @RequestBody LoginRequest request) {
        authService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }
}
