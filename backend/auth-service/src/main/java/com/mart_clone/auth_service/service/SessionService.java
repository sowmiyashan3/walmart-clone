package com.mart_clone.auth_service.service;

import com.mart_clone.auth_service.model.Session;
import com.mart_clone.auth_service.model.User;
import com.mart_clone.auth_service.repository.SessionRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionService {
    
    private final SessionRepository sessionRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    
    @Value("${session.timeout-minutes:30}")
    private int sessionTimeoutMinutes;
    
    @Value("${session.max-sessions-per-user:5}")
    private int maxSessionsPerUser;
    
    private static final String SESSION_PREFIX = "session:";
    
    @Transactional
    public Session createSession(User user, HttpServletRequest request) {
        // Check if user has too many active sessions
        List<Session> activeSessions = sessionRepository.findActiveSessionsByUserId(
            user.getId(), 
            LocalDateTime.now()
        );
        
        if (activeSessions.size() >= maxSessionsPerUser) {
            // Remove oldest session
            Session oldestSession = activeSessions.get(0);
            invalidateSession(oldestSession.getSessionId());
        }
        
        // Generate unique session ID
        String sessionId = generateSessionId();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(sessionTimeoutMinutes);
        
        // Create session entity
        Session session = Session.builder()
            .sessionId(sessionId)
            .user(user)
            .status(Session.SessionStatus.ACTIVE)
            .ipAddress(getClientIp(request))
            .userAgent(request.getHeader("User-Agent"))
            .expiresAt(expiresAt)
            .build();
        
        session = sessionRepository.save(session);
        
        // Store in Redis for fast access
        cacheSession(session);
        
        log.info("Created session {} for user {}", sessionId, user.getEmail());
        return session;
    }
    
    public Optional<Session> validateSession(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return Optional.empty();
        }
        
        // Try to get from cache first
        Session cachedSession = (Session) redisTemplate.opsForValue()
            .get(SESSION_PREFIX + sessionId);
        
        if (cachedSession != null && cachedSession.isActive()) {
            updateLastAccessed(sessionId);
            return Optional.of(cachedSession);
        }
        
        // If not in cache or expired, check database
        Optional<Session> sessionOpt = sessionRepository.findBySessionId(sessionId);
        
        if (sessionOpt.isEmpty()) {
            return Optional.empty();
        }
        
        Session session = sessionOpt.get();
        
        if (!session.isActive()) {
            return Optional.empty();
        }
        
        // Refresh cache
        cacheSession(session);
        updateLastAccessed(sessionId);
        
        return Optional.of(session);
    }
    
    @Transactional
    public void invalidateSession(String sessionId) {
        Optional<Session> sessionOpt = sessionRepository.findBySessionId(sessionId);
        
        if (sessionOpt.isPresent()) {
            Session session = sessionOpt.get();
            session.setStatus(Session.SessionStatus.LOGGED_OUT);
            sessionRepository.save(session);
            
            // Remove from cache
            redisTemplate.delete(SESSION_PREFIX + sessionId);
            
            log.info("Invalidated session {}", sessionId);
        }
    }
    
    @Transactional
    public void invalidateAllUserSessions(Long userId) {
        int count = sessionRepository.logoutAllUserSessions(userId);
        
        // Clear all user sessions from cache
        List<Session> sessions = sessionRepository.findByUserIdAndStatus(
            userId, 
            Session.SessionStatus.ACTIVE
        );
        
        sessions.forEach(session -> 
            redisTemplate.delete(SESSION_PREFIX + session.getSessionId())
        );
        
        log.info("Invalidated {} sessions for user {}", count, userId);
    }
    
    @Transactional
    public void refreshSession(String sessionId) {
        Optional<Session> sessionOpt = sessionRepository.findBySessionId(sessionId);
        
        if (sessionOpt.isPresent()) {
            Session session = sessionOpt.get();
            LocalDateTime newExpiresAt = LocalDateTime.now().plusMinutes(sessionTimeoutMinutes);
            session.setExpiresAt(newExpiresAt);
            sessionRepository.save(session);
            
            // Update cache
            cacheSession(session);
            
            log.debug("Refreshed session {}", sessionId);
        }
    }
    
    @Transactional
    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    public void cleanupExpiredSessions() {
        int count = sessionRepository.expireOldSessions(LocalDateTime.now());
        if (count > 0) {
            log.info("Cleaned up {} expired sessions", count);
        }
    }
    
    private void cacheSession(Session session) {
        redisTemplate.opsForValue().set(
            SESSION_PREFIX + session.getSessionId(),
            session,
            sessionTimeoutMinutes,
            TimeUnit.MINUTES
        );
    }
    
    @Transactional
    private void updateLastAccessed(String sessionId) {
        sessionRepository.findBySessionId(sessionId).ifPresent(session -> {
            session.setLastAccessedAt(LocalDateTime.now());
            sessionRepository.save(session);
        });
    }
    
    private String generateSessionId() {
        return UUID.randomUUID().toString() + "-" + System.currentTimeMillis();
    }
    
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
