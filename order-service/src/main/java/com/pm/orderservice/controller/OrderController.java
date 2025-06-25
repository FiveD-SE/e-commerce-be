package com.pm.orderservice.controller;

import com.pm.orderservice.dto.CreateOrderRequest;
import com.pm.orderservice.dto.OrderDto;
import com.pm.orderservice.dto.response.collection.CollectionResponse;
import com.pm.orderservice.model.OrderStatus;
import com.pm.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Order Management", description = "APIs for managing orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Create a new order from a user's cart")
    public ResponseEntity<OrderDto> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        log.info("Creating order for user ID: {}", request.getUserId());
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order details by order ID")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable @NotNull Long orderId) {
        log.info("Fetching order with ID: {}", orderId);
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all orders for a user")
    public ResponseEntity<CollectionResponse<OrderDto>> getOrdersByUserId(@PathVariable @NotNull Integer userId) {
        log.info("Fetching orders for user ID: {}", userId);
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    @PutMapping("/{orderId}/status")
    @Operation(summary = "Update order status")
    public ResponseEntity<OrderDto> updateOrderStatus(@PathVariable @NotNull Long orderId, @RequestParam @NotNull OrderStatus status) {
        log.info("Updating status for order ID: {} to {}", orderId, status);
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }
} 