package com.pm.authservice.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.authservice.dto.response.AuthResponse;
import com.pm.authservice.exception.AuthenticationException;
import com.pm.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
@ConditionalOnProperty(value = "services.user-service.mock-enabled", havingValue = "false", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    @Value("${services.user-service.url:http://localhost:8500}")
    private String userServiceUrl;

    @Value("${services.user-service.timeout:5000}")
    private int timeoutMs;

    @Override
    public boolean userExistsByEmail(String email) {
        log.debug("Checking if user exists by email: {}", maskEmail(email));

        try {
            WebClient webClient = webClientBuilder
                    .baseUrl(userServiceUrl)
                    .build();

            Boolean exists = webClient.get()
                    .uri("/api/users/exists/email/{email}", email)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .timeout(java.time.Duration.ofMillis(timeoutMs))
                    .block();

            log.debug("User exists by email {}: {}", maskEmail(email), exists);
            return Boolean.TRUE.equals(exists);

        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            }
            log.error("Error checking user existence by email {}: {}", maskEmail(email), e.getMessage());
            throw new AuthenticationException("Unable to verify user existence");
        } catch (Exception e) {
            log.error("Error checking user existence by email {}: {}", maskEmail(email), e.getMessage());
            throw new AuthenticationException("Unable to verify user existence");
        }
    }

    @Override
    public boolean userExistsByPhone(String phone) {
        log.debug("Checking if user exists by phone: {}", maskPhone(phone));

        try {
            WebClient webClient = webClientBuilder
                    .baseUrl(userServiceUrl)
                    .build();

            Boolean exists = webClient.get()
                    .uri("/api/users/exists/phone/{phone}", phone)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .timeout(java.time.Duration.ofMillis(timeoutMs))
                    .block();

            log.debug("User exists by phone {}: {}", maskPhone(phone), exists);
            return Boolean.TRUE.equals(exists);

        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            }
            log.error("Error checking user existence by phone {}: {}", maskPhone(phone), e.getMessage());
            throw new AuthenticationException("Unable to verify user existence");
        } catch (Exception e) {
            log.error("Error checking user existence by phone {}: {}", maskPhone(phone), e.getMessage());
            throw new AuthenticationException("Unable to verify user existence");
        }
    }

    @Override
    public AuthResponse.UserInfo getUserByEmail(String email) {
        log.debug("Getting user by email: {}", maskEmail(email));

        try {
            WebClient webClient = webClientBuilder
                    .baseUrl(userServiceUrl)
                    .build();

            ResponseEntity<Map<String, Object>> response = webClient.get()
                    .uri("/api/users/email/{email}", email)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .timeout(java.time.Duration.ofMillis(timeoutMs))
                    .block();

            if (response == null || response.getBody() == null) {
                throw new AuthenticationException("User not found");
            }

            return mapToUserInfo(response.getBody());

        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new AuthenticationException("User not found with email: " + maskEmail(email));
            }
            log.error("Error getting user by email {}: {}", maskEmail(email), e.getMessage());
            throw new AuthenticationException("Unable to retrieve user information");
        } catch (Exception e) {
            log.error("Error getting user by email {}: {}", maskEmail(email), e.getMessage());
            throw new AuthenticationException("Unable to retrieve user information");
        }
    }

    @Override
    public AuthResponse.UserInfo getUserByPhone(String phone) {
        log.debug("Getting user by phone: {}", maskPhone(phone));

        try {
            WebClient webClient = webClientBuilder
                    .baseUrl(userServiceUrl)
                    .build();

            ResponseEntity<Map<String, Object>> response = webClient.get()
                    .uri("/api/users/phone/{phone}", phone)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .timeout(java.time.Duration.ofMillis(timeoutMs))
                    .block();

            if (response == null || response.getBody() == null) {
                throw new AuthenticationException("User not found");
            }

            return mapToUserInfo(response.getBody());

        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new AuthenticationException("User not found with phone: " + maskPhone(phone));
            }
            log.error("Error getting user by phone {}: {}", maskPhone(phone), e.getMessage());
            throw new AuthenticationException("Unable to retrieve user information");
        } catch (Exception e) {
            log.error("Error getting user by phone {}: {}", maskPhone(phone), e.getMessage());
            throw new AuthenticationException("Unable to retrieve user information");
        }
    }

    @Override
    public AuthResponse.UserInfo getUserById(Integer userId) {
        log.debug("Getting user by ID: {}", userId);

        try {
            WebClient webClient = webClientBuilder
                    .baseUrl(userServiceUrl)
                    .build();

            ResponseEntity<Map<String, Object>> response = webClient.get()
                    .uri("/api/users/{userId}", userId)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .timeout(java.time.Duration.ofMillis(timeoutMs))
                    .block();

            if (response == null || response.getBody() == null) {
                throw new AuthenticationException("User not found");
            }

            return mapToUserInfo(response.getBody());

        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new AuthenticationException("User not found with ID: " + userId);
            }
            log.error("Error getting user by ID {}: {}", userId, e.getMessage());
            throw new AuthenticationException("Unable to retrieve user information");
        } catch (Exception e) {
            log.error("Error getting user by ID {}: {}", userId, e.getMessage());
            throw new AuthenticationException("Unable to retrieve user information");
        }
    }

    @Override
    public AuthResponse.UserInfo createUserByEmail(String email) {
        log.info("Creating user by email: {}", maskEmail(email));

        try {
            WebClient webClient = webClientBuilder
                    .baseUrl(userServiceUrl)
                    .build();

            Map<String, Object> createUserRequest = new HashMap<>();
            createUserRequest.put("email", email);
            createUserRequest.put("isEmailVerified", true);

            ResponseEntity<Map<String, Object>> response = webClient.post()
                    .uri("/api/users")
                    .bodyValue(createUserRequest)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .timeout(java.time.Duration.ofMillis(timeoutMs))
                    .block();

            if (response == null || response.getBody() == null) {
                throw new AuthenticationException("Failed to create user");
            }

            AuthResponse.UserInfo userInfo = mapToUserInfo(response.getBody());
            log.info("User created successfully: userId={}", userInfo.getUserId());
            return userInfo;

        } catch (WebClientResponseException e) {
            log.error("Error creating user by email {}: {}", maskEmail(email), e.getMessage());
            throw new AuthenticationException("Unable to create user account");
        } catch (Exception e) {
            log.error("Error creating user by email {}: {}", maskEmail(email), e.getMessage());
            throw new AuthenticationException("Unable to create user account");
        }
    }

    @Override
    public AuthResponse.UserInfo createUser(String email, String phone, String firstName, String lastName) {
        log.info("Creating user with email: {}, phone: {}", maskEmail(email), maskPhone(phone));

        try {
            WebClient webClient = webClientBuilder
                    .baseUrl(userServiceUrl)
                    .build();

            Map<String, Object> createUserRequest = new HashMap<>();
            if (email != null) createUserRequest.put("email", email);
            if (phone != null) createUserRequest.put("phone", phone);
            if (firstName != null) createUserRequest.put("firstName", firstName);
            if (lastName != null) createUserRequest.put("lastName", lastName);
            
            // Set verification status based on what was provided
            createUserRequest.put("isEmailVerified", email != null);
            createUserRequest.put("isPhoneVerified", phone != null);

            ResponseEntity<Map<String, Object>> response = webClient.post()
                    .uri("/api/users")
                    .bodyValue(createUserRequest)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .timeout(java.time.Duration.ofMillis(timeoutMs))
                    .block();

            if (response == null || response.getBody() == null) {
                throw new AuthenticationException("Failed to create user");
            }

            AuthResponse.UserInfo userInfo = mapToUserInfo(response.getBody());
            log.info("User created successfully: userId={}", userInfo.getUserId());
            return userInfo;

        } catch (WebClientResponseException e) {
            log.error("Error creating user: {}", e.getMessage());
            throw new AuthenticationException("Unable to create user account");
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage());
            throw new AuthenticationException("Unable to create user account");
        }
    }

    @Override
    public AuthResponse.UserInfo createUserByPhone(String phone) {
        log.info("Creating user by phone: {}", maskPhone(phone));

        try {
            WebClient webClient = webClientBuilder
                    .baseUrl(userServiceUrl)
                    .build();

            Map<String, Object> createUserRequest = new HashMap<>();
            createUserRequest.put("phone", phone);
            createUserRequest.put("isPhoneVerified", true);

            ResponseEntity<Map<String, Object>> response = webClient.post()
                    .uri("/api/users")
                    .bodyValue(createUserRequest)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .timeout(java.time.Duration.ofMillis(timeoutMs))
                    .block();

            if (response == null || response.getBody() == null) {
                throw new AuthenticationException("Failed to create user");
            }

            AuthResponse.UserInfo userInfo = mapToUserInfo(response.getBody());
            log.info("User created successfully: userId={}", userInfo.getUserId());
            return userInfo;

        } catch (WebClientResponseException e) {
            log.error("Error creating user by phone {}: {}", maskPhone(phone), e.getMessage());
            throw new AuthenticationException("Unable to create user account");
        } catch (Exception e) {
            log.error("Error creating user by phone {}: {}", maskPhone(phone), e.getMessage());
            throw new AuthenticationException("Unable to create user account");
        }
    }

    private AuthResponse.UserInfo mapToUserInfo(Map<String, Object> userData) {
        try {
            return AuthResponse.UserInfo.builder()
                    .userId(getIntegerValue(userData, "userId"))
                    .email(getStringValue(userData, "email"))
                    .phone(getStringValue(userData, "phone"))
                    .firstName(getStringValue(userData, "firstName"))
                    .lastName(getStringValue(userData, "lastName"))
                    .isEmailVerified(getBooleanValue(userData, "isEmailVerified"))
                    .isPhoneVerified(getBooleanValue(userData, "isPhoneVerified"))
                    .build();
        } catch (Exception e) {
            log.error("Error mapping user data to UserInfo: {}", e.getMessage());
            throw new AuthenticationException("Unable to process user information");
        }
    }

    private Integer getIntegerValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) return null;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            return Integer.valueOf(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String getStringValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value != null ? value.toString() : null;
    }

    private Boolean getBooleanValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) return false;
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof String) return Boolean.parseBoolean((String) value);
        return false;
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        
        String[] parts = email.split("@");
        if (parts.length != 2) {
            return email;
        }
        
        String localPart = parts[0];
        String domain = parts[1];
        
        if (localPart.length() <= 2) {
            return "*".repeat(localPart.length()) + "@" + domain;
        }
        
        return localPart.substring(0, 2) + "*".repeat(localPart.length() - 2) + "@" + domain;
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return phone;
        }
        
        return "*".repeat(phone.length() - 4) + phone.substring(phone.length() - 4);
    }
} 