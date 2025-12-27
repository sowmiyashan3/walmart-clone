package com.mart_clone.auth_service.controller;

import com.mart_clone.auth_service.dto.*;
import com.mart_clone.auth_service.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${cors.allowed-origins}", allowCredentials = "true")
public class AuthController {
    
    private final AuthService authService;
    
    @Value("${server.servlet.session.cookie.name:WALMART_SESSION}")
    private String sessionCookieName;
    
    @Value("${server.servlet.session.cookie.max-age:1800}")
    private int cookieMaxAge;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        
        log.info("Register request received for email: {}", request.getEmail());
        
        AuthResponse response = authService.register(request, httpRequest);
        
        // Set session cookie
        setSessionCookie(httpResponse, response.getSessionId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        
        log.info("Login request received for email: {}", request.getEmail());
        
        AuthResponse response = authService.login(request, httpRequest);
        
        // Set session cookie
        setSessionCookie(httpResponse, response.getSessionId());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(
            HttpServletRequest request,
            HttpServletResponse response) {
        
        String sessionId = getSessionIdFromRequest(request);
        
        if (sessionId != null) {
            authService.logout(sessionId);
            clearSessionCookie(response);
        }
        
        return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
    }
    
    @PostMapping("/logout-all")
    public ResponseEntity<MessageResponse> logoutAll(
            HttpServletRequest request,
            HttpServletResponse response) {
        
        String sessionId = getSessionIdFromRequest(request);
        
        if (sessionId != null) {
            UserDTO user = authService.getCurrentUser(sessionId);
            authService.logoutAll(user.getId());
            clearSessionCookie(response);
        }
        
        return ResponseEntity.ok(new MessageResponse("All sessions logged out successfully"));
    }
    
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(HttpServletRequest request) {
        String sessionId = getSessionIdFromRequest(request);
        
        if (sessionId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        UserDTO user = authService.getCurrentUser(sessionId);
        return ResponseEntity.ok(user);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<MessageResponse> refreshSession(
            HttpServletRequest request,
            HttpServletResponse response) {
        
        String sessionId = getSessionIdFromRequest(request);
        
        if (sessionId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        authService.refreshSession(sessionId);
        
        // Reset cookie expiration
        setSessionCookie(response, sessionId);
        
        return ResponseEntity.ok(new MessageResponse("Session refreshed successfully"));
    }
    
    @GetMapping("/validate")
    public ResponseEntity<SessionValidationResponse> validateSession(
            HttpServletRequest request) {
        
        String sessionId = getSessionIdFromRequest(request);
        
        if (sessionId == null) {
            return ResponseEntity.ok(
                SessionValidationResponse.builder()
                    .valid(false)
                    .message("No session found")
                    .build()
            );
        }
        
        try {
            UserDTO user = authService.getCurrentUser(sessionId);
            return ResponseEntity.ok(
                SessionValidationResponse.builder()
                    .valid(true)
                    .user(user)
                    .message("Session is valid")
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.ok(
                SessionValidationResponse.builder()
                    .valid(false)
                    .message("Invalid or expired session")
                    .build()
            );
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<MessageResponse> health() {
        return ResponseEntity.ok(new MessageResponse("Auth service is running"));
    }
    
    // Helper methods
    private String getSessionIdFromRequest(HttpServletRequest request) {
        // Try to get from cookie first
        if (request.getCookies() != null) {
            Optional<Cookie> sessionCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> sessionCookieName.equals(cookie.getName()))
                .findFirst();
            
            if (sessionCookie.isPresent()) {
                return sessionCookie.get().getValue();
            }
        }
        
        // Fallback to header
        return request.getHeader("X-Session-ID");
    }
    
    private void setSessionCookie(HttpServletResponse response, String sessionId) {
        Cookie cookie = new Cookie(sessionCookieName, sessionId);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to true in production with HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(cookieMaxAge);
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);
    }
    
    private void clearSessionCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(sessionCookieName, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}