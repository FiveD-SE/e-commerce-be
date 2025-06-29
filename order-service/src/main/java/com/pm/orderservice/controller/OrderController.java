package com.pm.orderservice.controller;

import com.pm.orderservice.dto.*;
import com.pm.orderservice.dto.response.collection.CollectionResponse;
import com.pm.orderservice.model.OrderStatus;
import com.pm.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Order Management", description = "APIs for managing orders")
public class OrderController {
    private final OrderService orderService;

    // ==================== Order Creation and Retrieval ====================

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

    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Get order details by order number")
    public ResponseEntity<OrderDto> getOrderByOrderNumber(@PathVariable @NotNull String orderNumber) {
        log.info("Fetching order with order number: {}", orderNumber);
        return ResponseEntity.ok(orderService.getOrderByOrderNumber(orderNumber));
    }

    // ==================== Order Listing and Filtering ====================

    @GetMapping
    @Operation(summary = "Get all orders with pagination")
    public ResponseEntity<CollectionResponse<OrderDto>> getAllOrders(
            @PageableDefault(size = 20, sort = "orderDate", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Fetching all orders with pagination: {}", pageable);
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all orders for a user")
    public ResponseEntity<CollectionResponse<OrderDto>> getOrdersByUserId(
            @PathVariable @NotNull Integer userId,
            @PageableDefault(size = 20, sort = "orderDate", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Fetching orders for user ID: {} with pagination: {}", userId, pageable);
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId, pageable));
    }

    @GetMapping("/user/{userId}/summaries")
    @Operation(summary = "Get order summaries for a user")
    public ResponseEntity<CollectionResponse<OrderSummaryDto>> getOrderSummariesByUserId(
            @PathVariable @NotNull Integer userId,
            @PageableDefault(size = 20, sort = "orderDate", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Fetching order summaries for user ID: {} with pagination: {}", userId, pageable);
        return ResponseEntity.ok(orderService.getOrderSummariesByUserId(userId, pageable));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get orders by status")
    public ResponseEntity<CollectionResponse<OrderDto>> getOrdersByStatus(
            @PathVariable @NotNull OrderStatus status,
            @PageableDefault(size = 20, sort = "orderDate", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Fetching orders by status: {} with pagination: {}", status, pageable);
        return ResponseEntity.ok(orderService.getOrdersByStatus(status, pageable));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get orders by date range")
    public ResponseEntity<CollectionResponse<OrderDto>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
            @PageableDefault(size = 20, sort = "orderDate", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Fetching orders between {} and {} with pagination: {}", startDate, endDate, pageable);
        return ResponseEntity.ok(orderService.getOrdersByDateRange(startDate, endDate, pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "Search orders")
    public ResponseEntity<CollectionResponse<OrderDto>> searchOrders(
            @RequestParam String searchTerm,
            @PageableDefault(size = 20, sort = "orderDate", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Searching orders with term: {} and pagination: {}", searchTerm, pageable);
        return ResponseEntity.ok(orderService.searchOrders(searchTerm, pageable));
    }

    // ==================== Order Status Management ====================

    @PutMapping("/{orderId}/status")
    @Operation(summary = "Update order status")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @PathVariable @NotNull Long orderId,
            @RequestBody @Valid UpdateOrderStatusRequest request) {
        log.info("Updating status for order ID: {} to {}", orderId, request.getStatus());
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, request));
    }

    @PutMapping("/{orderId}/confirm")
    @Operation(summary = "Confirm order")
    public ResponseEntity<OrderDto> confirmOrder(@PathVariable @NotNull Long orderId) {
        log.info("Confirming order ID: {}", orderId);
        return ResponseEntity.ok(orderService.confirmOrder(orderId));
    }

    @PutMapping("/{orderId}/ship")
    @Operation(summary = "Ship order")
    public ResponseEntity<OrderDto> shipOrder(
            @PathVariable @NotNull Long orderId,
            @RequestParam @NotNull String trackingNumber,
            @RequestParam(required = false) String shippingMethod) {
        log.info("Shipping order ID: {} with tracking number: {}", orderId, trackingNumber);
        return ResponseEntity.ok(orderService.shipOrder(orderId, trackingNumber, shippingMethod));
    }

    @PutMapping("/{orderId}/deliver")
    @Operation(summary = "Mark order as delivered")
    public ResponseEntity<OrderDto> deliverOrder(@PathVariable @NotNull Long orderId) {
        log.info("Delivering order ID: {}", orderId);
        return ResponseEntity.ok(orderService.deliverOrder(orderId));
    }

    @PutMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel order")
    public ResponseEntity<OrderDto> cancelOrder(
            @PathVariable @NotNull Long orderId,
            @RequestParam(required = false) String reason) {
        log.info("Cancelling order ID: {} with reason: {}", orderId, reason);
        return ResponseEntity.ok(orderService.cancelOrder(orderId, reason));
    }

    // ==================== Order Analytics ====================

    @GetMapping("/count/status/{status}")
    @Operation(summary = "Count orders by status")
    public ResponseEntity<Long> countOrdersByStatus(@PathVariable @NotNull OrderStatus status) {
        log.info("Counting orders by status: {}", status);
        return ResponseEntity.ok(orderService.countOrdersByStatus(status));
    }

    @GetMapping("/count/user/{userId}")
    @Operation(summary = "Count orders by user")
    public ResponseEntity<Long> countOrdersByUserId(@PathVariable @NotNull Integer userId) {
        log.info("Counting orders for user ID: {}", userId);
        return ResponseEntity.ok(orderService.countOrdersByUserId(userId));
    }

    // ==================== Order Validation ====================

    @GetMapping("/{orderId}/can-update-status/{newStatus}")
    @Operation(summary = "Check if order status can be updated")
    public ResponseEntity<Boolean> canUpdateOrderStatus(
            @PathVariable @NotNull Long orderId,
            @PathVariable @NotNull OrderStatus newStatus) {
        log.info("Checking if order ID: {} can be updated to status: {}", orderId, newStatus);
        return ResponseEntity.ok(orderService.canUpdateOrderStatus(orderId, newStatus));
    }

    @GetMapping("/{orderId}/can-cancel")
    @Operation(summary = "Check if order can be cancelled")
    public ResponseEntity<Boolean> canCancelOrder(@PathVariable @NotNull Long orderId) {
        log.info("Checking if order ID: {} can be cancelled", orderId);
        return ResponseEntity.ok(orderService.canCancelOrder(orderId));
    }

    // ==================== Order Items Management ====================

    @PostMapping("/{orderId}/items")
    @Operation(summary = "Add item to order")
    public ResponseEntity<OrderDto> addItemToOrder(
            @PathVariable @NotNull Long orderId,
            @RequestBody @Valid OrderItemDto orderItemDto) {
        log.info("Adding item to order ID: {}", orderId);
        return ResponseEntity.ok(orderService.addItemToOrder(orderId, orderItemDto));
    }

    @DeleteMapping("/{orderId}/items/{orderItemId}")
    @Operation(summary = "Remove item from order")
    public ResponseEntity<OrderDto> removeItemFromOrder(
            @PathVariable @NotNull Long orderId,
            @PathVariable @NotNull Long orderItemId) {
        log.info("Removing item {} from order ID: {}", orderItemId, orderId);
        return ResponseEntity.ok(orderService.removeItemFromOrder(orderId, orderItemId));
    }

    @PutMapping("/{orderId}/items/{orderItemId}")
    @Operation(summary = "Update order item")
    public ResponseEntity<OrderDto> updateOrderItem(
            @PathVariable @NotNull Long orderId,
            @PathVariable @NotNull Long orderItemId,
            @RequestBody @Valid OrderItemDto orderItemDto) {
        log.info("Updating item {} in order ID: {}", orderItemId, orderId);
        return ResponseEntity.ok(orderService.updateOrderItem(orderId, orderItemId, orderItemDto));
    }
}