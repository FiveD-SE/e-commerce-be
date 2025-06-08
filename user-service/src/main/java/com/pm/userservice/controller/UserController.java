package com.pm.userservice.controller;

import com.pm.userservice.dto.UserDto;
import com.pm.userservice.dto.response.collection.CollectionResponse;
import com.pm.userservice.service.UserService;
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
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @GetMapping
    public ResponseEntity<CollectionResponse<UserDto>> findAll() {
        log.info("Fetching all users");
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> findById(
            @PathVariable @NotNull(message = "User ID must not be null") @Valid Integer userId) {
        log.info("Fetching user with ID: {}", userId);
        return ResponseEntity.ok(userService.findById(userId));
    }

    @PostMapping
    public ResponseEntity<UserDto> save(
            @RequestBody @NotNull(message = "Input must not be null") @Valid UserDto userDto) {
        log.info("Saving new user: {}", userDto.getEmail());
        UserDto savedUser = userService.save(userDto);
        return ResponseEntity.created(URI.create("/api/users/" + savedUser.getUserId())).body(savedUser);
    }

    @PutMapping
    public ResponseEntity<UserDto> update(
            @RequestBody @NotNull(message = "Input must not be null") @Valid UserDto userDto) {
        log.info("Updating user: {}", userDto.getEmail());
        return ResponseEntity.ok(userService.update(userDto));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> update(
            @PathVariable @NotNull(message = "User ID must not be null") @Valid Integer userId,
            @RequestBody @NotNull(message = "Input must not be null") @Valid UserDto userDto) {
        log.info("Updating user with ID: {}", userId);
        return ResponseEntity.ok(userService.update(userId, userDto));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteById(
            @PathVariable @NotNull(message = "User ID must not be null") @Valid Integer userId) {
        log.info("Deleting user with ID: {}", userId);
        userService.deleteById(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDto> findByUsername(
            @PathVariable @NotNull(message = "Username must not be null") @Valid String username) {
        log.info("Fetching user with username: {}", username);
        return ResponseEntity.ok(userService.findByUsername(username));
    }
}
