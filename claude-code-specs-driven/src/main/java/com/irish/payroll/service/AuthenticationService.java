package com.irish.payroll.service;

import com.irish.payroll.dto.request.LoginRequest;
import com.irish.payroll.dto.response.JwtResponse;
import com.irish.payroll.entity.User;
import com.irish.payroll.repository.UserRepository;
import com.irish.payroll.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service for authentication operations.
 */
@Service
public class AuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    /**
     * Authenticate user and return JWT token.
     *
     * @param request Login request
     * @return JWT response
     */
    public JwtResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtils.generateToken(authentication.getName());

        return new JwtResponse(token, request.getUsername(), jwtExpirationMs);
    }

    /**
     * Register a new user (for testing purposes).
     *
     * @param request Login request with username and password
     */
    public void register(LoginRequest request) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getUsername() + "@irishpayroll.com");
        user.setEnabled(true);
        user.setCreatedDate(LocalDateTime.now());
        user.setLastModifiedDate(LocalDateTime.now());
        userRepository.save(user);
    }
}
