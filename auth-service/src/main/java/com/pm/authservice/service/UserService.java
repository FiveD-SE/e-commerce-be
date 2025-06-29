package com.pm.authservice.service;

import com.pm.authservice.dto.response.AuthResponse;

public interface UserService {

    /**
     * Validate if user exists by email
     */
    boolean userExistsByEmail(String email);

    /**
     * Validate if user exists by phone
     */
    boolean userExistsByPhone(String phone);

    /**
     * Get user information by email
     */
    AuthResponse.UserInfo getUserByEmail(String email);

    /**
     * Get user information by phone
     */
    AuthResponse.UserInfo getUserByPhone(String phone);

    /**
     * Get user information by user ID
     */
    AuthResponse.UserInfo getUserById(Integer userId);

    /**
     * Create a new user (for registration flow)
     */
    AuthResponse.UserInfo createUser(String email, String phone, String firstName, String lastName);

    /**
     * Create a new user by email
     */
    AuthResponse.UserInfo createUserByEmail(String email);

    /**
     * Create a new user by phone
     */
    AuthResponse.UserInfo createUserByPhone(String phone);
} 