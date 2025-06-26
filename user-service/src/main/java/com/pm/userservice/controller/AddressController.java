package com.pm.userservice.controller;

import com.pm.userservice.dto.AddressDto;
import com.pm.userservice.dto.response.collection.CollectionResponse;
import com.pm.userservice.service.AddressService;
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
@RequestMapping("/api/address")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Address Management", description = "APIs for managing user addresses")
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    @Operation(summary = "Get all addresses")
    public ResponseEntity<CollectionResponse<AddressDto>> findAll() {
        return ResponseEntity.ok(addressService.findAll());
    }

    @GetMapping("/{addressId}")
    @Operation(summary = "Get address by ID")
    public ResponseEntity<AddressDto> findById(
            @PathVariable @NotNull(message = "Address ID must not be null") @Valid Integer addressId) {
        return ResponseEntity.ok(addressService.findById(addressId));
    }

    @PostMapping
    @Operation(summary = "Create a new address")
    public ResponseEntity<AddressDto> save(
            @RequestBody @NotNull(message = "Input must not be null") @Valid AddressDto addressDto) {
        AddressDto savedAddress = addressService.save(addressDto);
        return ResponseEntity.created(URI.create("/api/address/" + savedAddress.getAddressId())).body(savedAddress);
    }

    @PutMapping
    @Operation(summary = "Update an existing address")
    public ResponseEntity<AddressDto> update(
            @RequestBody @NotNull(message = "Input must not be null") @Valid AddressDto addressDto) {
        return ResponseEntity.ok(addressService.update(addressDto));
    }

    @PutMapping("/{addressId}")
    @Operation(summary = "Update an existing address by ID")
    public ResponseEntity<AddressDto> update(
            @PathVariable @NotNull(message = "Address ID must not be null") @Valid Integer addressId,
            @RequestBody @NotNull(message = "Input must not be null") @Valid AddressDto addressDto) {
        return ResponseEntity.ok(addressService.update(addressId, addressDto));
    }

    @DeleteMapping("/{addressId}")
    @Operation(summary = "Delete an address by ID")
    public ResponseEntity<Void> deleteById(
            @PathVariable @NotNull(message = "Address ID must not be null") @Valid Integer addressId) {
        addressService.deleteById(addressId);
        return ResponseEntity.noContent().build();
    }
}
