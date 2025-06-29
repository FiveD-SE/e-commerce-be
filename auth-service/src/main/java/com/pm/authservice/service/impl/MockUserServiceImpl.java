package com.pm.authservice.service.impl;

import com.pm.authservice.dto.response.AuthResponse;
import com.pm.authservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@ConditionalOnProperty(value = "services.user-service.mock-enabled", havingValue = "true")
@Slf4j
public class MockUserServiceImpl implements UserService {

    private final AtomicInteger userIdCounter = new AtomicInteger(1000);
    private final Map<String, AuthResponse.UserInfo> emailUsers = new HashMap<>();
    private final Map<String, AuthResponse.UserInfo> phoneUsers = new HashMap<>();
    private final Map<Integer, AuthResponse.UserInfo> idUsers = new HashMap<>();

    @Override
    public boolean userExistsByEmail(String email) {
        log.debug("Mock: Checking if user exists by email: {}", maskEmail(email));
        // For testing, assume users don't exist unless they've been created
        boolean exists = emailUsers.containsKey(email);
        log.debug("Mock: User exists by email {}: {}", maskEmail(email), exists);
        return exists;
    }

    @Override
    public boolean userExistsByPhone(String phone) {
        log.debug("Mock: Checking if user exists by phone: {}", maskPhone(phone));
        // For testing, assume users don't exist unless they've been created
        boolean exists = phoneUsers.containsKey(phone);
        log.debug("Mock: User exists by phone {}: {}", maskPhone(phone), exists);
        return exists;
    }

    @Override
    public AuthResponse.UserInfo getUserByEmail(String email) {
        log.debug("Mock: Getting user by email: {}", maskEmail(email));
        AuthResponse.UserInfo user = emailUsers.get(email);
        if (user == null) {
            log.debug("Mock: User not found with email: {}", maskEmail(email));
            return null;
        }
        log.debug("Mock: Found user by email: {}", user.getUserId());
        return user;
    }

    @Override
    public AuthResponse.UserInfo getUserByPhone(String phone) {
        log.debug("Mock: Getting user by phone: {}", maskPhone(phone));
        AuthResponse.UserInfo user = phoneUsers.get(phone);
        if (user == null) {
            log.debug("Mock: User not found with phone: {}", maskPhone(phone));
            return null;
        }
        log.debug("Mock: Found user by phone: {}", user.getUserId());
        return user;
    }

    @Override
    public AuthResponse.UserInfo getUserById(Integer userId) {
        log.debug("Mock: Getting user by ID: {}", userId);
        AuthResponse.UserInfo user = idUsers.get(userId);
        if (user == null) {
            log.debug("Mock: User not found with ID: {}", userId);
            return null;
        }
        log.debug("Mock: Found user by ID: {}", userId);
        return user;
    }

    @Override
    public AuthResponse.UserInfo createUser(String email, String phone, String firstName, String lastName) {
        log.debug("Mock: Creating user with email: {} and phone: {}", maskEmail(email), maskPhone(phone));
        
        Integer userId = userIdCounter.getAndIncrement();
        AuthResponse.UserInfo user = AuthResponse.UserInfo.builder()
                .userId(userId)
                .email(email)
                .phone(phone)
                .firstName(firstName != null ? firstName : "Test")
                .lastName(lastName != null ? lastName : "User")
                .isEmailVerified(false)
                .isPhoneVerified(false)
                .build();

        // Store in all maps
        if (email != null) {
            emailUsers.put(email, user);
        }
        if (phone != null) {
            phoneUsers.put(phone, user);
        }
        idUsers.put(userId, user);

        log.info("Mock: Created user with ID: {}", userId);
        return user;
    }

    @Override
    public AuthResponse.UserInfo createUserByEmail(String email) {
        log.debug("Mock: Creating user by email: {}", maskEmail(email));
        return createUser(email, null, "Test", "User");
    }

    @Override
    public AuthResponse.UserInfo createUserByPhone(String phone) {
        log.debug("Mock: Creating user by phone: {}", maskPhone(phone));
        return createUser(null, phone, "Test", "User");
    }

    private String maskEmail(String email) {
        if (email == null || email.length() < 3) {
            return email;
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 0) {
            return email.substring(0, 2) + "**";
        }
        return email.substring(0, 2) + "**" + email.substring(atIndex);
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return phone;
        }
        return "*******" + phone.substring(phone.length() - 4);
    }
} 