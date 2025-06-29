package com.pm.orderservice.service.impl;

import com.pm.orderservice.dto.*;
import com.pm.orderservice.dto.response.collection.CollectionResponse;
import com.pm.orderservice.mapper.OrderItemMapper;
import com.pm.orderservice.mapper.OrderMapper;
import com.pm.orderservice.model.Order;
import com.pm.orderservice.model.OrderItem;
import com.pm.orderservice.model.OrderStatus;
import com.pm.orderservice.repository.OrderItemRepository;
import com.pm.orderservice.repository.OrderRepository;
import com.pm.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final RestTemplate restTemplate;

    // ==================== Order Creation and Management ====================

    @Override
    public OrderDto createOrder(CreateOrderRequest request) {
        log.info("Creating order for user ID: {}", request.getUserId());

        // Fetch cart from cart-service
        String cartServiceUrl = "http://cart-service:8300/api/carts/user/" + request.getUserId();
        var cartResponse = restTemplate.getForObject(cartServiceUrl, Object.class); // Replace Object with CartDto if shared
        // TODO: Map cartResponse to order items (simulate for now)
        // In production, use a shared DTO module or OpenAPI generator
        Set<OrderItem> orderItems = Set.of(); // TODO: Map from cartResponse
        BigDecimal totalAmount = BigDecimal.ZERO; // TODO: Calculate from cartResponse

        Order order = Order.builder()
                .userId(request.getUserId())
                .userEmail(request.getUserEmail())
                .userPhone(request.getUserPhone())
                .shippingAddress(request.getShippingAddress())
                .shippingCity(request.getShippingCity())
                .shippingPostalCode(request.getShippingPostalCode())
                .shippingCountry(request.getShippingCountry())
                .billingAddress(request.getBillingAddress())
                .billingCity(request.getBillingCity())
                .billingPostalCode(request.getBillingPostalCode())
                .billingCountry(request.getBillingCountry())
                .status(OrderStatus.PENDING)
                .subtotal(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .shippingFee(BigDecimal.ZERO)
                .discountAmount(BigDecimal.ZERO)
                .totalAmount(totalAmount)
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus("PENDING")
                .shippingMethod(request.getShippingMethod())
                .notes(request.getNotes())
                .isGift(request.getIsGift())
                .giftMessage(request.getGiftMessage())
                .orderItems(orderItems)
                .build();

        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with ID: {}", savedOrder.getOrderId());
        return orderMapper.toDTO(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long orderId) {
        log.info("Fetching order by ID: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        return orderMapper.toDTO(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrderByOrderNumber(String orderNumber) {
        log.info("Fetching order by order number: {}", orderNumber);
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found with order number: " + orderNumber));
        return orderMapper.toDTO(order);
    }

    // ==================== Order Listing and Filtering ====================

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<OrderDto> getAllOrders(Pageable pageable) {
        log.info("Fetching all orders with pagination: {}", pageable);
        Page<Order> orderPage = orderRepository.findAll(pageable);
        List<OrderDto> orders = orderPage.getContent().stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());

        return CollectionResponse.<OrderDto>builder()
                .data(orders)
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .currentPage(orderPage.getNumber())
                .pageSize(orderPage.getSize())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<OrderDto> getOrdersByUserId(Integer userId, Pageable pageable) {
        log.info("Fetching orders for user ID: {} with pagination: {}", userId, pageable);
        Page<Order> orderPage = orderRepository.findByUserId(userId, pageable);
        List<OrderDto> orders = orderPage.getContent().stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());

        return CollectionResponse.<OrderDto>builder()
                .data(orders)
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .currentPage(orderPage.getNumber())
                .pageSize(orderPage.getSize())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<OrderDto> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        log.info("Fetching orders by status: {} with pagination: {}", status, pageable);
        Page<Order> orderPage = orderRepository.findByStatus(status, pageable);
        List<OrderDto> orders = orderPage.getContent().stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());

        return CollectionResponse.<OrderDto>builder()
                .data(orders)
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .currentPage(orderPage.getNumber())
                .pageSize(orderPage.getSize())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<OrderDto> getOrdersByDateRange(Instant startDate, Instant endDate, Pageable pageable) {
        log.info("Fetching orders between {} and {} with pagination: {}", startDate, endDate, pageable);
        Page<Order> orderPage = orderRepository.findByOrderDateBetween(startDate, endDate, pageable);
        List<OrderDto> orders = orderPage.getContent().stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());

        return CollectionResponse.<OrderDto>builder()
                .data(orders)
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .currentPage(orderPage.getNumber())
                .pageSize(orderPage.getSize())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<OrderSummaryDto> getOrderSummariesByUserId(Integer userId, Pageable pageable) {
        log.info("Fetching order summaries for user ID: {} with pagination: {}", userId, pageable);
        Page<Order> orderPage = orderRepository.findByUserId(userId, pageable);
        List<OrderSummaryDto> orderSummaries = orderPage.getContent().stream()
                .map(this::convertToOrderSummary)
                .collect(Collectors.toList());

        return CollectionResponse.<OrderSummaryDto>builder()
                .data(orderSummaries)
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .currentPage(orderPage.getNumber())
                .pageSize(orderPage.getSize())
                .build();
    }

    // ==================== Order Status Management ====================

    @Override
    public OrderDto updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        log.info("Updating order status for order ID: {} to {}", orderId, request.getStatus());

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        if (!canUpdateOrderStatus(orderId, request.getStatus())) {
            throw new RuntimeException("Cannot update order status from " + order.getStatus() + " to " + request.getStatus());
        }

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(request.getStatus());

        // Update relevant dates based on status
        Instant now = Instant.now();
        switch (request.getStatus()) {
            case PENDING:
                // No specific date update needed
                break;
            case CONFIRMED:
                order.setConfirmedDate(now);
                break;
            case PROCESSING:
                // No specific date field, but could add processing_date if needed
                break;
            case SHIPPED:
                order.setShippedDate(now);
                if (request.getTrackingNumber() != null) {
                    order.setTrackingNumber(request.getTrackingNumber());
                }
                break;
            case DELIVERED:
                order.setDeliveredDate(now);
                order.setActualDeliveryDate(now);
                break;
            case CANCELLED:
                order.setCancelledDate(now);
                if (request.getCancellationReason() != null) {
                    order.setCancellationReason(request.getCancellationReason());
                }
                break;
            case REFUNDED:
                // Could add refunded_date if needed
                break;
        }

        if (request.getAdminNotes() != null) {
            order.setAdminNotes(request.getAdminNotes());
        }

        Order updatedOrder = orderRepository.save(order);
        log.info("Order status updated from {} to {} for order ID: {}", oldStatus, request.getStatus(), orderId);
        return orderMapper.toDTO(updatedOrder);
    }

    @Override
    public OrderDto confirmOrder(Long orderId) {
        log.info("Confirming order ID: {}", orderId);
        UpdateOrderStatusRequest request = UpdateOrderStatusRequest.builder()
                .status(OrderStatus.CONFIRMED)
                .build();
        return updateOrderStatus(orderId, request);
    }

    @Override
    public OrderDto shipOrder(Long orderId, String trackingNumber, String shippingMethod) {
        log.info("Shipping order ID: {} with tracking number: {}", orderId, trackingNumber);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        if (!canBeShipped(order)) {
            throw new RuntimeException("Order cannot be shipped. Current status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.SHIPPED);
        order.setShippedDate(Instant.now());
        order.setTrackingNumber(trackingNumber);
        if (shippingMethod != null) {
            order.setShippingMethod(shippingMethod);
        }

        Order updatedOrder = orderRepository.save(order);
        log.info("Order shipped successfully: {}", orderId);
        return orderMapper.toDTO(updatedOrder);
    }

    @Override
    public OrderDto deliverOrder(Long orderId) {
        log.info("Delivering order ID: {}", orderId);
        UpdateOrderStatusRequest request = UpdateOrderStatusRequest.builder()
                .status(OrderStatus.DELIVERED)
                .build();
        return updateOrderStatus(orderId, request);
    }

    @Override
    public OrderDto cancelOrder(Long orderId, String reason) {
        log.info("Cancelling order ID: {} with reason: {}", orderId, reason);
        UpdateOrderStatusRequest request = UpdateOrderStatusRequest.builder()
                .status(OrderStatus.CANCELLED)
                .cancellationReason(reason)
                .build();
        return updateOrderStatus(orderId, request);
    }

    // ==================== Order Search and Analytics ====================

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<OrderDto> searchOrders(String searchTerm, Pageable pageable) {
        log.info("Searching orders with term: {} and pagination: {}", searchTerm, pageable);
        Page<Order> orderPage = orderRepository.searchOrders(searchTerm, pageable);
        List<OrderDto> orders = orderPage.getContent().stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());

        return CollectionResponse.<OrderDto>builder()
                .data(orders)
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .currentPage(orderPage.getNumber())
                .pageSize(orderPage.getSize())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public long countOrdersByStatus(OrderStatus status) {
        return orderRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public long countOrdersByUserId(Integer userId) {
        return orderRepository.countByUserId(userId);
    }

    // ==================== Order Validation ====================

    @Override
    @Transactional(readOnly = true)
    public boolean canUpdateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        OrderStatus currentStatus = order.getStatus();

        // Define valid status transitions
        switch (currentStatus) {
            case PENDING:
                return newStatus == OrderStatus.CONFIRMED || newStatus == OrderStatus.CANCELLED;
            case CONFIRMED:
                return newStatus == OrderStatus.PROCESSING || newStatus == OrderStatus.CANCELLED;
            case PROCESSING:
                return newStatus == OrderStatus.SHIPPED || newStatus == OrderStatus.CANCELLED;
            case SHIPPED:
                return newStatus == OrderStatus.DELIVERED;
            case DELIVERED:
                return newStatus == OrderStatus.REFUNDED;
            case CANCELLED:
            case REFUNDED:
                return false; // Final states
            default:
                return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canCancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        return order.canBeCancelled();
    }

    // ==================== Order Items Management ====================

    @Override
    public OrderDto addItemToOrder(Long orderId, OrderItemDto orderItemDto) {
        log.info("Adding item to order ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        if (!canModifyOrderItems(order)) {
            throw new RuntimeException("Cannot modify items for order in status: " + order.getStatus());
        }

        OrderItem orderItem = orderItemMapper.toEntity(orderItemDto);
        orderItem.setOrder(order);

        OrderItem savedItem = orderItemRepository.save(orderItem);

        // Recalculate order totals
        recalculateOrderTotals(order);
        Order updatedOrder = orderRepository.save(order);

        log.info("Item added to order ID: {}", orderId);
        return orderMapper.toDTO(updatedOrder);
    }

    @Override
    public OrderDto removeItemFromOrder(Long orderId, Long orderItemId) {
        log.info("Removing item {} from order ID: {}", orderItemId, orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        if (!canModifyOrderItems(order)) {
            throw new RuntimeException("Cannot modify items for order in status: " + order.getStatus());
        }

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("Order item not found with ID: " + orderItemId));

        if (!orderItem.getOrder().getOrderId().equals(orderId)) {
            throw new RuntimeException("Order item does not belong to the specified order");
        }

        orderItemRepository.delete(orderItem);

        // Recalculate order totals
        recalculateOrderTotals(order);
        Order updatedOrder = orderRepository.save(order);

        log.info("Item removed from order ID: {}", orderId);
        return orderMapper.toDTO(updatedOrder);
    }

    @Override
    public OrderDto updateOrderItem(Long orderId, Long orderItemId, OrderItemDto orderItemDto) {
        log.info("Updating item {} in order ID: {}", orderItemId, orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        if (!canModifyOrderItems(order)) {
            throw new RuntimeException("Cannot modify items for order in status: " + order.getStatus());
        }

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("Order item not found with ID: " + orderItemId));

        if (!orderItem.getOrder().getOrderId().equals(orderId)) {
            throw new RuntimeException("Order item does not belong to the specified order");
        }

        // Update order item fields
        orderItem.setQuantity(orderItemDto.getQuantity());
        orderItem.setUnitPrice(orderItemDto.getUnitPrice());
        orderItem.setDiscountAmount(orderItemDto.getDiscountAmount());
        orderItem.setTaxAmount(orderItemDto.getTaxAmount());
        orderItem.setNotes(orderItemDto.getNotes());

        OrderItem updatedItem = orderItemRepository.save(orderItem);

        // Recalculate order totals
        recalculateOrderTotals(order);
        Order updatedOrder = orderRepository.save(order);

        log.info("Item updated in order ID: {}", orderId);
        return orderMapper.toDTO(updatedOrder);
    }

    // ==================== Helper Methods ====================

    private boolean canBeShipped(Order order) {
        return order.canBeShipped();
    }

    private boolean canModifyOrderItems(Order order) {
        return order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.CONFIRMED;
    }

    private void recalculateOrderTotals(Order order) {
        if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            order.setSubtotal(BigDecimal.ZERO);
            order.setTotalAmount(BigDecimal.ZERO);
            return;
        }

        BigDecimal subtotal = order.getOrderItems().stream()
                .map(OrderItem::getFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal taxAmount = order.getTaxAmount() != null ? order.getTaxAmount() : BigDecimal.ZERO;
        BigDecimal shippingFee = order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO;
        BigDecimal discountAmount = order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO;

        order.setSubtotal(subtotal);
        order.setTotalAmount(subtotal.add(taxAmount).add(shippingFee).subtract(discountAmount));
    }

    private OrderSummaryDto convertToOrderSummary(Order order) {
        int totalItems = order.getOrderItems() != null ?
                order.getOrderItems().stream()
                        .mapToInt(OrderItem::getQuantity)
                        .sum() : 0;

        return OrderSummaryDto.builder()
                .orderId(order.getOrderId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUserId())
                .userEmail(order.getUserEmail())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .paymentStatus(order.getPaymentStatus())
                .shippingMethod(order.getShippingMethod())
                .trackingNumber(order.getTrackingNumber())
                .orderDate(order.getOrderDate())
                .estimatedDeliveryDate(order.getEstimatedDeliveryDate())
                .totalItems(totalItems)
                .build();
    }
}