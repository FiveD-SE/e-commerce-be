package com.pm.userservice.controller;

import com.pm.userservice.dto.AddressDto;
import com.pm.userservice.dto.response.collection.CollectionResponse;
import com.pm.userservice.service.AddressService;
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
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<CollectionResponse<AddressDto>> findAll() {
        return ResponseEntity.ok(addressService.findAll());
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<AddressDto> findById(
            @PathVariable @NotNull(message = "Address ID must not be null") @Valid Integer addressId) {
        return ResponseEntity.ok(addressService.findById(addressId));
    }

    @PostMapping
    public ResponseEntity<AddressDto> save(
            @RequestBody @NotNull(message = "Input must not be null") @Valid AddressDto addressDto) {
        AddressDto savedAddress = addressService.save(addressDto);
        return ResponseEntity.created(URI.create("/api/address/" + savedAddress.getAddressId())).body(savedAddress);
    }

    @PutMapping
    public ResponseEntity<AddressDto> update(
            @RequestBody @NotNull(message = "Input must not be null") @Valid AddressDto addressDto) {
        return ResponseEntity.ok(addressService.update(addressDto));
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<AddressDto> update(
            @PathVariable @NotNull(message = "Address ID must not be null") @Valid Integer addressId,
            @RequestBody @NotNull(message = "Input must not be null") @Valid AddressDto addressDto) {
        return ResponseEntity.ok(addressService.update(addressId, addressDto));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteById(
            @PathVariable @NotNull(message = "Address ID must not be null") @Valid Integer addressId) {
        addressService.deleteById(addressId);
        return ResponseEntity.noContent().build();
    }
}
