package com.mart_clone.auth_service.repository;

import com.mart_clone.auth_service.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, String> {
    
    Optional<Session> findBySessionId(String sessionId);
    
    List<Session> findByUserIdAndStatus(Long userId, Session.SessionStatus status);
    
    @Query("SELECT s FROM Session s WHERE s.user.id = :userId AND s.status = 'ACTIVE' AND s.expiresAt > :now")
    List<Session> findActiveSessionsByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    @Modifying
    @Query("UPDATE Session s SET s.status = 'EXPIRED' WHERE s.expiresAt < :now AND s.status = 'ACTIVE'")
    int expireOldSessions(@Param("now") LocalDateTime now);
    
    @Modifying
    @Query("UPDATE Session s SET s.status = 'LOGGED_OUT' WHERE s.user.id = :userId AND s.status = 'ACTIVE'")
    int logoutAllUserSessions(@Param("userId") Long userId);
    
    long countByUserIdAndStatus(Long userId, Session.SessionStatus status);
}