package com.pm.authservice.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Test configuration that disables Redis components for testing
 */
@Configuration
@Profile("test")
@ConditionalOnProperty(value = "spring.data.redis.enabled", havingValue = "false", matchIfMissing = true)
public class TestRedisConfig {
    // This class intentionally empty - it exists to disable Redis configuration in test profile
} 