package com.pm.userservice.controller;

import com.pm.userservice.dto.VerificationTokenDto;
import com.pm.userservice.dto.response.collection.CollectionResponse;
import com.pm.userservice.service.VerificationTokenService;
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
@RequestMapping("/api/verificationTokens")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Email Verification", description = "APIs for managing email verification tokens")
public class VerificationTokenController {

    private final VerificationTokenService verificationTokenService;

    @GetMapping
    @Operation(summary = "Get all verification tokens")
    public ResponseEntity<CollectionResponse<VerificationTokenDto>> findAll() {
        log.info("Fetching all verification tokens");
        return ResponseEntity.ok(verificationTokenService.findAll());
    }

    @GetMapping("/{verificationTokenId}")
    @Operation(summary = "Get a verification token by ID")
    public ResponseEntity<VerificationTokenDto> findById(
            @PathVariable @NotNull(message = "Verification Token ID must not be null") @Valid Integer verificationTokenId) {
        log.info("Fetching verification token with ID: {}", verificationTokenId);
        return ResponseEntity.ok(verificationTokenService.findById(verificationTokenId));
    }

    @PostMapping
    @Operation(summary = "Create a new verification token")
    public ResponseEntity<VerificationTokenDto> save(
            @RequestBody @NotNull(message = "Input must not be null") @Valid VerificationTokenDto verificationTokenDto) {
        log.info("Saving new verification token: {}", verificationTokenDto.getToken());
        VerificationTokenDto savedToken = verificationTokenService.save(verificationTokenDto);
        return ResponseEntity.created(URI.create("/api/verificationTokens/" + savedToken.getVerificationTokenId())).body(savedToken);
    }

    @PutMapping
    @Operation(summary = "Update a verification token")
    public ResponseEntity<VerificationTokenDto> update(
            @RequestBody @NotNull(message = "Input must not be null") @Valid VerificationTokenDto verificationTokenDto) {
        log.info("Updating verification token: {}", verificationTokenDto.getToken());
        return ResponseEntity.ok(verificationTokenService.update(verificationTokenDto));
    }

    @PutMapping("/{verificationTokenId}")
    @Operation(summary = "Update a verification token by ID")
    public ResponseEntity<VerificationTokenDto> update(
            @PathVariable @NotNull(message = "Verification Token ID must not be null") @Valid Integer verificationTokenId,
            @RequestBody @NotNull(message = "Input must not be null") @Valid VerificationTokenDto verificationTokenDto) {
        log.info("Updating verification token with ID: {}", verificationTokenId);
        return ResponseEntity.ok(verificationTokenService.update(verificationTokenId, verificationTokenDto));
    }

    @DeleteMapping("/{verificationTokenId}")
    @Operation(summary = "Delete a verification token by ID")
    public ResponseEntity<Void> deleteById(
            @PathVariable @NotNull(message = "Verification Token ID must not be null") @Valid Integer verificationTokenId) {
        log.info("Deleting verification token with ID: {}", verificationTokenId);
        verificationTokenService.deleteById(verificationTokenId);
        return ResponseEntity.noContent().build();
    }
}
