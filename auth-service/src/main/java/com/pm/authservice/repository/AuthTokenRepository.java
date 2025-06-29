package com.pm.authservice.repository;

import com.pm.authservice.model.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {

    Optional<AuthToken> findByAccessTokenAndIsActiveTrue(String accessToken);

    Optional<AuthToken> findByRefreshTokenAndIsActiveTrue(String refreshToken);

    List<AuthToken> findByUserIdAndIsActiveTrue(Integer userId);

    @Modifying
    @Query("UPDATE AuthToken a SET a.isActive = false WHERE a.userId = :userId")
    void deactivateAllTokensByUserId(@Param("userId") Integer userId);

    @Modifying
    @Query("DELETE FROM AuthToken a WHERE (a.accessTokenExpiresAt < :now OR a.refreshTokenExpiresAt < :now)")
    long deleteExpiredTokens(@Param("now") Instant now);

    @Modifying
    @Query("DELETE FROM AuthToken a WHERE a.isActive = false AND a.createdAt < :threshold")
    long deleteOldInactiveTokens(@Param("threshold") Instant threshold);

    @Modifying
    @Query("UPDATE AuthToken a SET a.isActive = false WHERE a.refreshToken = :refreshToken")
    void deactivateTokenByRefreshToken(@Param("refreshToken") String refreshToken);



    @Query("SELECT COUNT(a) FROM AuthToken a WHERE a.userId = :userId AND a.isActive = true")
    long countActiveTokensByUserId(@Param("userId") Integer userId);

} 