package com.pm.userservice.controller;

import com.pm.userservice.dto.AddressDto;
import com.pm.userservice.dto.SubscriptionRequest;
import com.pm.userservice.dto.UserDto;
import com.pm.userservice.dto.UserProfileDto;
import com.pm.userservice.dto.response.collection.CollectionResponse;
import com.pm.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing user profiles and information")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all users")
    public ResponseEntity<CollectionResponse<UserDto>> findAll() {
        log.info("Fetching all users");
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get a user by ID")
    public ResponseEntity<UserDto> findById(
            @PathVariable @NotNull(message = "User ID must not be null") @Valid Integer userId) {
        log.info("Fetching user with ID: {}", userId);
        return ResponseEntity.ok(userService.findById(userId));
    }

    @PostMapping
    @Operation(summary = "Create a new user")
    public ResponseEntity<UserDto> save(
            @RequestBody @NotNull(message = "Input must not be null") @Valid UserDto userDto) {
        log.info("Saving new user: {}", userDto.getEmail());
        UserDto savedUser = userService.save(userDto);
        return ResponseEntity.created(URI.create("/api/users/" + savedUser.getUserId())).body(savedUser);
    }

    @PutMapping
    @Operation(summary = "Update an existing user")
    public ResponseEntity<UserDto> update(
            @RequestBody @NotNull(message = "Input must not be null") @Valid UserDto userDto) {
        log.info("Updating user: {}", userDto.getEmail());
        return ResponseEntity.ok(userService.update(userDto));
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update a user by ID")
    public ResponseEntity<UserDto> update(
            @PathVariable @NotNull(message = "User ID must not be null") @Valid Integer userId,
            @RequestBody @NotNull(message = "Input must not be null") @Valid UserDto userDto) {
        log.info("Updating user with ID: {}", userId);
        return ResponseEntity.ok(userService.update(userId, userDto));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete a user by ID")
    public ResponseEntity<Void> deleteById(
            @PathVariable @NotNull(message = "User ID must not be null") @Valid Integer userId) {
        log.info("Deleting user with ID: {}", userId);
        userService.deleteById(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Get a user by username")
    public ResponseEntity<UserDto> findByUsername(
            @PathVariable @NotNull(message = "Username must not be null") @Valid String username) {
        log.info("Fetching user with username: {}", username);
        return ResponseEntity.ok(userService.findByUsername(username));
    }

    // Profile management endpoints
    @GetMapping("/{userId}/profile")
    @Operation(summary = "Get user profile")
    public ResponseEntity<UserProfileDto> getUserProfile(
            @PathVariable @NotNull(message = "User ID must not be null") @Valid Integer userId) {
        log.info("Fetching profile for user ID: {}", userId);
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @PutMapping("/{userId}/profile")
    @Operation(summary = "Update user profile")
    public ResponseEntity<UserProfileDto> updateUserProfile(
            @PathVariable @NotNull(message = "User ID must not be null") @Valid Integer userId,
            @RequestBody @NotNull(message = "Profile data must not be null") @Valid UserProfileDto userProfileDto) {
        log.info("Updating profile for user ID: {}", userId);
        return ResponseEntity.ok(userService.updateUserProfile(userId, userProfileDto));
    }

    // Address management endpoints
    @GetMapping("/{userId}/addresses")
    @Operation(summary = "Get user addresses")
    public ResponseEntity<CollectionResponse<AddressDto>> getUserAddresses(
            @PathVariable @NotNull(message = "User ID must not be null") @Valid Integer userId) {
        log.info("Fetching addresses for user ID: {}", userId);
        return ResponseEntity.ok(userService.getUserAddresses(userId));
    }

    @PostMapping("/{userId}/addresses")
    @Operation(summary = "Add user address")
    public ResponseEntity<AddressDto> addUserAddress(
            @PathVariable @NotNull(message = "User ID must not be null") @Valid Integer userId,
            @RequestBody @NotNull(message = "Address data must not be null") @Valid AddressDto addressDto) {
        log.info("Adding address for user ID: {}", userId);
        AddressDto savedAddress = userService.addUserAddress(userId, addressDto);
        return ResponseEntity.created(URI.create("/api/users/" + userId + "/addresses/" + savedAddress.getAddressId())).body(savedAddress);
    }

    // Subscription management endpoints
    @PutMapping("/{userId}/subscriptions")
    @Operation(summary = "Update user subscriptions")
    public ResponseEntity<UserDto> updateSubscriptions(
            @PathVariable @NotNull(message = "User ID must not be null") @Valid Integer userId,
            @RequestBody @NotNull(message = "Subscription data must not be null") @Valid SubscriptionRequest subscriptionRequest) {
        log.info("Updating subscriptions for user ID: {}", userId);
        return ResponseEntity.ok(userService.updateSubscriptions(userId, subscriptionRequest));
    }

    @PostMapping("/{userId}/subscribe/email")
    @Operation(summary = "Subscribe to email notifications")
    public ResponseEntity<UserDto> subscribeToEmail(
            @PathVariable @NotNull(message = "User ID must not be null") @Valid Integer userId) {
        log.info("Subscribing user {} to email notifications", userId);
        return ResponseEntity.ok(userService.subscribeToEmail(userId));
    }

    @DeleteMapping("/{userId}/subscribe/email")
    @Operation(summary = "Unsubscribe from email notifications")
    public ResponseEntity<UserDto> unsubscribeFromEmail(
            @PathVariable @NotNull(message = "User ID must not be null") @Valid Integer userId) {
        log.info("Unsubscribing user {} from email notifications", userId);
        return ResponseEntity.ok(userService.unsubscribeFromEmail(userId));
    }

    @PostMapping("/{userId}/subscribe/phone")
    @Operation(summary = "Subscribe to SMS notifications")
    public ResponseEntity<UserDto> subscribeToSms(
            @PathVariable @NotNull(message = "User ID must not be null") @Valid Integer userId) {
        log.info("Subscribing user {} to SMS notifications", userId);
        return ResponseEntity.ok(userService.subscribeToSms(userId));
    }

    @DeleteMapping("/{userId}/subscribe/phone")
    @Operation(summary = "Unsubscribe from SMS notifications")
    public ResponseEntity<UserDto> unsubscribeFromSms(
            @PathVariable @NotNull(message = "User ID must not be null") @Valid Integer userId) {
        log.info("Unsubscribing user {} from SMS notifications", userId);
        return ResponseEntity.ok(userService.unsubscribeFromSms(userId));
    }
}
