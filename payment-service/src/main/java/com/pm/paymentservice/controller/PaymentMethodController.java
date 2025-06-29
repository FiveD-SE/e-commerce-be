package com.pm.paymentservice.controller;

import com.pm.paymentservice.dto.CreatePaymentMethodRequest;
import com.pm.paymentservice.dto.PaymentMethodDto;
import com.pm.paymentservice.dto.response.collection.CollectionResponse;
import com.pm.paymentservice.model.PaymentGateway;
import com.pm.paymentservice.service.PaymentMethodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-methods")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment Method Management", description = "APIs for managing payment methods")
public class PaymentMethodController {
    
    private final PaymentMethodService paymentMethodService;

    // ==================== Payment Method Creation and Management ====================
    
    @PostMapping
    @Operation(summary = "Create a new payment method")
    public ResponseEntity<PaymentMethodDto> createPaymentMethod(@RequestBody @Valid CreatePaymentMethodRequest request) {
        log.info("Creating payment method for user ID: {}", request.getUserId());
        return ResponseEntity.ok(paymentMethodService.createPaymentMethod(request));
    }

    @GetMapping("/{methodId}")
    @Operation(summary = "Get payment method details by method ID")
    public ResponseEntity<PaymentMethodDto> getPaymentMethodById(@PathVariable @NotNull Long methodId) {
        log.info("Fetching payment method with ID: {}", methodId);
        return ResponseEntity.ok(paymentMethodService.getPaymentMethodById(methodId));
    }

    @PutMapping("/{methodId}")
    @Operation(summary = "Update payment method")
    public ResponseEntity<PaymentMethodDto> updatePaymentMethod(
            @PathVariable @NotNull Long methodId,
            @RequestBody @Valid PaymentMethodDto paymentMethodDto) {
        log.info("Updating payment method ID: {}", methodId);
        return ResponseEntity.ok(paymentMethodService.updatePaymentMethod(methodId, paymentMethodDto));
    }

    @DeleteMapping("/{methodId}")
    @Operation(summary = "Delete payment method")
    public ResponseEntity<Void> deletePaymentMethod(@PathVariable @NotNull Long methodId) {
        log.info("Deleting payment method ID: {}", methodId);
        paymentMethodService.deletePaymentMethod(methodId);
        return ResponseEntity.ok().build();
    }

    // ==================== User Payment Methods ====================
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all payment methods for a user")
    public ResponseEntity<List<PaymentMethodDto>> getPaymentMethodsByUserId(@PathVariable @NotNull Integer userId) {
        log.info("Fetching payment methods for user ID: {}", userId);
        return ResponseEntity.ok(paymentMethodService.getPaymentMethodsByUserId(userId));
    }

    @GetMapping("/user/{userId}/paginated")
    @Operation(summary = "Get payment methods for a user with pagination")
    public ResponseEntity<CollectionResponse<PaymentMethodDto>> getPaymentMethodsByUserIdPaginated(
            @PathVariable @NotNull Integer userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Fetching payment methods for user ID: {} with pagination: {}", userId, pageable);
        return ResponseEntity.ok(paymentMethodService.getPaymentMethodsByUserId(userId, pageable));
    }

    @GetMapping("/user/{userId}/active")
    @Operation(summary = "Get active payment methods for a user")
    public ResponseEntity<List<PaymentMethodDto>> getActivePaymentMethodsByUserId(@PathVariable @NotNull Integer userId) {
        log.info("Fetching active payment methods for user ID: {}", userId);
        return ResponseEntity.ok(paymentMethodService.getActivePaymentMethodsByUserId(userId));
    }

    @GetMapping("/user/{userId}/default")
    @Operation(summary = "Get default payment method for a user")
    public ResponseEntity<PaymentMethodDto> getDefaultPaymentMethodByUserId(@PathVariable @NotNull Integer userId) {
        log.info("Fetching default payment method for user ID: {}", userId);
        return ResponseEntity.ok(paymentMethodService.getDefaultPaymentMethodByUserId(userId));
    }

    // ==================== Payment Method Types and Gateways ====================
    
    @GetMapping("/user/{userId}/type/{type}")
    @Operation(summary = "Get payment methods by user and type")
    public ResponseEntity<List<PaymentMethodDto>> getPaymentMethodsByUserIdAndType(
            @PathVariable @NotNull Integer userId,
            @PathVariable @NotNull String type) {
        log.info("Fetching payment methods for user ID: {} and type: {}", userId, type);
        return ResponseEntity.ok(paymentMethodService.getPaymentMethodsByUserIdAndType(userId, type));
    }

    @GetMapping("/user/{userId}/gateway/{gateway}")
    @Operation(summary = "Get payment methods by user and gateway")
    public ResponseEntity<List<PaymentMethodDto>> getPaymentMethodsByUserIdAndGateway(
            @PathVariable @NotNull Integer userId,
            @PathVariable @NotNull PaymentGateway gateway) {
        log.info("Fetching payment methods for user ID: {} and gateway: {}", userId, gateway);
        return ResponseEntity.ok(paymentMethodService.getPaymentMethodsByUserIdAndGateway(userId, gateway));
    }

    @GetMapping("/user/{userId}/cards")
    @Operation(summary = "Get cards for a user")
    public ResponseEntity<List<PaymentMethodDto>> getCardsByUserId(@PathVariable @NotNull Integer userId) {
        log.info("Fetching cards for user ID: {}", userId);
        return ResponseEntity.ok(paymentMethodService.getCardsByUserId(userId));
    }

    // ==================== Payment Method Status Management ====================
    
    @PutMapping("/{methodId}/activate")
    @Operation(summary = "Activate payment method")
    public ResponseEntity<PaymentMethodDto> activatePaymentMethod(@PathVariable @NotNull Long methodId) {
        log.info("Activating payment method ID: {}", methodId);
        return ResponseEntity.ok(paymentMethodService.activatePaymentMethod(methodId));
    }

    @PutMapping("/{methodId}/deactivate")
    @Operation(summary = "Deactivate payment method")
    public ResponseEntity<PaymentMethodDto> deactivatePaymentMethod(@PathVariable @NotNull Long methodId) {
        log.info("Deactivating payment method ID: {}", methodId);
        return ResponseEntity.ok(paymentMethodService.deactivatePaymentMethod(methodId));
    }

    @PutMapping("/{methodId}/set-default")
    @Operation(summary = "Set payment method as default")
    public ResponseEntity<PaymentMethodDto> setAsDefaultPaymentMethod(@PathVariable @NotNull Long methodId) {
        log.info("Setting payment method as default: {}", methodId);
        return ResponseEntity.ok(paymentMethodService.setAsDefaultPaymentMethod(methodId));
    }

    @PutMapping("/{methodId}/verify")
    @Operation(summary = "Verify payment method")
    public ResponseEntity<PaymentMethodDto> verifyPaymentMethod(@PathVariable @NotNull Long methodId) {
        log.info("Verifying payment method ID: {}", methodId);
        return ResponseEntity.ok(paymentMethodService.verifyPaymentMethod(methodId));
    }

    // ==================== Payment Method Validation ====================
    
    @GetMapping("/{methodId}/is-valid")
    @Operation(summary = "Check if payment method is valid")
    public ResponseEntity<Boolean> isPaymentMethodValid(@PathVariable @NotNull Long methodId) {
        log.info("Checking if payment method ID: {} is valid", methodId);
        return ResponseEntity.ok(paymentMethodService.isPaymentMethodValid(methodId));
    }

    @GetMapping("/{methodId}/is-expired")
    @Operation(summary = "Check if card is expired")
    public ResponseEntity<Boolean> isCardExpired(@PathVariable @NotNull Long methodId) {
        log.info("Checking if card ID: {} is expired", methodId);
        return ResponseEntity.ok(paymentMethodService.isCardExpired(methodId));
    }

    @GetMapping("/user/{userId}/expiring-cards")
    @Operation(summary = "Get expiring cards for a user")
    public ResponseEntity<List<PaymentMethodDto>> getExpiringCards(
            @PathVariable @NotNull Integer userId,
            @RequestParam(defaultValue = "3") int months) {
        log.info("Fetching expiring cards for user ID: {} within {} months", userId, months);
        return ResponseEntity.ok(paymentMethodService.getExpiringCards(userId, months));
    }

    // ==================== Payment Method Analytics ====================
    
    @GetMapping("/count/user/{userId}")
    @Operation(summary = "Count payment methods by user")
    public ResponseEntity<Long> countPaymentMethodsByUserId(@PathVariable @NotNull Integer userId) {
        log.info("Counting payment methods for user ID: {}", userId);
        return ResponseEntity.ok(paymentMethodService.countPaymentMethodsByUserId(userId));
    }

    @GetMapping("/count/user/{userId}/active")
    @Operation(summary = "Count active payment methods by user")
    public ResponseEntity<Long> countActivePaymentMethodsByUserId(@PathVariable @NotNull Integer userId) {
        log.info("Counting active payment methods for user ID: {}", userId);
        return ResponseEntity.ok(paymentMethodService.countActivePaymentMethodsByUserId(userId));
    }

    @GetMapping("/count/user/{userId}/type/{type}")
    @Operation(summary = "Count payment methods by user and type")
    public ResponseEntity<Long> countPaymentMethodsByUserIdAndType(
            @PathVariable @NotNull Integer userId,
            @PathVariable @NotNull String type) {
        log.info("Counting payment methods for user ID: {} and type: {}", userId, type);
        return ResponseEntity.ok(paymentMethodService.countPaymentMethodsByUserIdAndType(userId, type));
    }
}
