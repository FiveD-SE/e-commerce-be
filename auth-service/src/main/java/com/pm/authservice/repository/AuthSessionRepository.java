package com.pm.authservice.repository;

import com.pm.authservice.model.AuthSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuthSessionRepository extends JpaRepository<AuthSession, Long> {

    Optional<AuthSession> findBySessionTokenAndIsActiveTrue(String sessionToken);

    List<AuthSession> findByUserIdAndIsActiveTrue(Integer userId);

    @Modifying
    @Query("UPDATE AuthSession s SET s.isActive = false WHERE s.userId = :userId")
    void deactivateAllSessionsByUserId(@Param("userId") Integer userId);

    @Modifying
    @Query("UPDATE AuthSession s SET s.isActive = false WHERE s.sessionToken = :sessionToken")
    void deactivateSessionByToken(@Param("sessionToken") String sessionToken);

    @Modifying
    @Query("DELETE FROM AuthSession s WHERE s.expiresAt < :expiredBefore")
    void deleteExpiredSessions(@Param("expiredBefore") Instant expiredBefore);

    @Query("SELECT COUNT(s) FROM AuthSession s WHERE s.userId = :userId AND s.isActive = true")
    long countActiveSessionsByUserId(@Param("userId") Integer userId);

} 