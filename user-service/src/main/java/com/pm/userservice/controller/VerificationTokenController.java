package com.pm.userservice.controller;

import com.pm.userservice.dto.VerificationTokenDto;
import com.pm.userservice.dto.response.collection.CollectionResponse;
import com.pm.userservice.service.VerificationTokenService;
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
public class VerificationTokenController {

    private final VerificationTokenService verificationTokenService;

    @GetMapping
    public ResponseEntity<CollectionResponse<VerificationTokenDto>> findAll() {
        log.info("Fetching all verification tokens");
        return ResponseEntity.ok(verificationTokenService.findAll());
    }

    @GetMapping("/{verificationTokenId}")
    public ResponseEntity<VerificationTokenDto> findById(
            @PathVariable @NotNull(message = "Verification Token ID must not be null") @Valid Integer verificationTokenId) {
        log.info("Fetching verification token with ID: {}", verificationTokenId);
        return ResponseEntity.ok(verificationTokenService.findById(verificationTokenId));
    }

    @PostMapping
    public ResponseEntity<VerificationTokenDto> save(
            @RequestBody @NotNull(message = "Input must not be null") @Valid VerificationTokenDto verificationTokenDto) {
        log.info("Saving new verification token: {}", verificationTokenDto.getToken());
        VerificationTokenDto savedToken = verificationTokenService.save(verificationTokenDto);
        return ResponseEntity.created(URI.create("/api/verificationTokens/" + savedToken.getVerificationTokenId())).body(savedToken);
    }

    @PutMapping
    public ResponseEntity<VerificationTokenDto> update(
            @RequestBody @NotNull(message = "Input must not be null") @Valid VerificationTokenDto verificationTokenDto) {
        log.info("Updating verification token: {}", verificationTokenDto.getToken());
        return ResponseEntity.ok(verificationTokenService.update(verificationTokenDto));
    }

    @PutMapping("/{verificationTokenId}")
    public ResponseEntity<VerificationTokenDto> update(
            @PathVariable @NotNull(message = "Verification Token ID must not be null") @Valid Integer verificationTokenId,
            @RequestBody @NotNull(message = "Input must not be null") @Valid VerificationTokenDto verificationTokenDto) {
        log.info("Updating verification token with ID: {}", verificationTokenId);
        return ResponseEntity.ok(verificationTokenService.update(verificationTokenId, verificationTokenDto));
    }

    @DeleteMapping("/{verificationTokenId}")
    public ResponseEntity<Void> deleteById(
            @PathVariable @NotNull(message = "Verification Token ID must not be null") @Valid Integer verificationTokenId) {
        log.info("Deleting verification token with ID: {}", verificationTokenId);
        verificationTokenService.deleteById(verificationTokenId);
        return ResponseEntity.noContent().build();
    }
}
