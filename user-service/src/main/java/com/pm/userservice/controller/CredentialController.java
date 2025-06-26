package com.pm.userservice.controller;

import com.pm.userservice.dto.CredentialDto;
import com.pm.userservice.dto.response.collection.CollectionResponse;
import com.pm.userservice.service.CredentialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/credentials")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "APIs for managing user credentials and authentication")
public class CredentialController {

    private final CredentialService credentialService;

    @GetMapping
    @Operation(summary = "Get all credentials")
    public ResponseEntity<CollectionResponse<CredentialDto>> findAll() {
        log.info("Fetching all credentials");
        return ResponseEntity.ok(credentialService.findAll());
    }

    @GetMapping("/{credentialId}")
    @Operation(summary = "Get a credential by ID")
    public ResponseEntity<CredentialDto> findById(
            @PathVariable @NotNull(message = "Credential ID must not be null") @Valid Integer credentialId) {
        log.info("Fetching credential with ID: {}", credentialId);
        return ResponseEntity.ok(credentialService.findById(credentialId));
    }

    @PostMapping
    @Operation(summary = "Create a new credential")
    public ResponseEntity<CredentialDto> save(
            @RequestBody @NotNull(message = "Input must not be null") @Valid CredentialDto credentialDto) {
        log.info("Saving new credential: {}", credentialDto.getUsername());
        CredentialDto savedCredential = credentialService.save(credentialDto);
        return ResponseEntity.created(URI.create("/api/credentials/" + savedCredential.getCredentialId())).body(savedCredential);
    }

    @PutMapping
    @Operation(summary = "Update an existing credential")
    public ResponseEntity<CredentialDto> update(
            @RequestBody @NotNull(message = "Input must not be null") @Valid CredentialDto credentialDto) {
        log.info("Updating credential: {}", credentialDto.getUsername());
        return ResponseEntity.ok(credentialService.update(credentialDto));
    }

    @PutMapping("/{credentialId}")
    @Operation(summary = "Update a credential by ID")
    public ResponseEntity<CredentialDto> update(
            @PathVariable @NotNull(message = "Credential ID must not be null") @Valid Integer credentialId,
            @RequestBody @NotNull(message = "Input must not be null") @Valid CredentialDto credentialDto) {
        log.info("Updating credential with ID: {}", credentialId);
        return ResponseEntity.ok(credentialService.update(credentialId, credentialDto));
    }

    @DeleteMapping("/{credentialId}")
    @Operation(summary = "Delete a credential by ID")
    public ResponseEntity<Void> deleteById(
            @PathVariable @NotNull(message = "Credential ID must not be null") @Valid Integer credentialId) {
        log.info("Deleting credential with ID: {}", credentialId);
        credentialService.deleteById(credentialId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Get a credential by username")
    public ResponseEntity<CredentialDto> findByUsername(
            @PathVariable @NotNull(message = "Username must not be null") @Valid String username) {
        log.info("Fetching credential with username: {}", username);
        return ResponseEntity.ok(credentialService.findByUsername(username));
    }
}
