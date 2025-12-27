package com.mart_clone.auth_service.service;

import com.mart_clone.auth_service.dto.AuthResponse;
import com.mart_clone.auth_service.dto.LoginRequest;
import com.mart_clone.auth_service.dto.RegisterRequest;
import com.mart_clone.auth_service.dto.UserDTO;
import com.mart_clone.auth_service.exception.AuthenticationException;
import com.mart_clone.auth_service.model.Role;
import com.mart_clone.auth_service.model.Session;
import com.mart_clone.auth_service.model.User;
import com.mart_clone.auth_service.repository.RoleRepository;
import com.mart_clone.auth_service.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SessionService sessionService;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public AuthResponse register(RegisterRequest request, HttpServletRequest httpRequest) {
        log.info("Attempting to register user with email: {}", request.getEmail());
        
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthenticationException("Email already registered");
        }
        
        // Create user
        User user = User.builder()
            .email(request.getEmail())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .phone(request.getPhone())
            .isActive(true)
            .build();
        
        // Assign default role
        Role userRole = roleRepository.findByName(Role.RoleType.ROLE_USER.name())
            .orElseGet(() -> {
                Role newRole = Role.builder()
                    .name(Role.RoleType.ROLE_USER.name())
                    .build();
                return roleRepository.save(newRole);
            });
        
        user.addRole(userRole);
        user = userRepository.save(user);
        
        // Create session
        Session session = sessionService.createSession(user, httpRequest);
        
        log.info("Successfully registered user: {}", user.getEmail());
        
        return AuthResponse.builder()
            .sessionId(session.getSessionId())
            .user(convertToDTO(user))
            .message("Registration successful")
            .expiresAt(session.getExpiresAt())
            .build();
    }
    
    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        log.info("Attempting login for user: {}", request.getEmail());
        
        // Find user
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new AuthenticationException("Invalid email or password"));
        
        // Check if user is active
        if (!user.getIsActive()) {
            throw new AuthenticationException("Account is disabled");
        }
        
        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AuthenticationException("Invalid email or password");
        }
        
        // Create session
        Session session = sessionService.createSession(user, httpRequest);
        
        log.info("Successfully logged in user: {}", user.getEmail());
        
        return AuthResponse.builder()
            .sessionId(session.getSessionId())
            .user(convertToDTO(user))
            .message("Login successful")
            .expiresAt(session.getExpiresAt())
            .build();
    }
    
    @Transactional
    public void logout(String sessionId) {
        log.info("Logging out session: {}", sessionId);
        sessionService.invalidateSession(sessionId);
    }
    
    @Transactional
    public void logoutAll(Long userId) {
        log.info("Logging out all sessions for user: {}", userId);
        sessionService.invalidateAllUserSessions(userId);
    }
    
    public UserDTO getCurrentUser(String sessionId) {
        Session session = sessionService.validateSession(sessionId)
            .orElseThrow(() -> new AuthenticationException("Invalid or expired session"));
        
        return convertToDTO(session.getUser());
    }
    
    @Transactional
    public void refreshSession(String sessionId) {
        sessionService.refreshSession(sessionId);
    }
    
    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
            .id(user.getId())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .phone(user.getPhone())
            .roles(user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet()))
            .createdAt(user.getCreatedAt())
            .build();
    }
}
