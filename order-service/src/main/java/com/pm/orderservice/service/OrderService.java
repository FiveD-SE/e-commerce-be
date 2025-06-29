package com.pm.orderservice.service;

import com.pm.orderservice.dto.*;
import com.pm.orderservice.dto.response.collection.CollectionResponse;
import com.pm.orderservice.model.OrderStatus;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

public interface OrderService {

    // Order Creation and Management
    OrderDto createOrder(CreateOrderRequest request);
    OrderDto getOrderById(Long orderId);
    OrderDto getOrderByOrderNumber(String orderNumber);

    // Order Listing and Filtering
    CollectionResponse<OrderDto> getAllOrders(Pageable pageable);
    CollectionResponse<OrderDto> getOrdersByUserId(Integer userId, Pageable pageable);
    CollectionResponse<OrderDto> getOrdersByStatus(OrderStatus status, Pageable pageable);
    CollectionResponse<OrderDto> getOrdersByDateRange(Instant startDate, Instant endDate, Pageable pageable);
    CollectionResponse<OrderSummaryDto> getOrderSummariesByUserId(Integer userId, Pageable pageable);

    // Order Status Management
    OrderDto updateOrderStatus(Long orderId, UpdateOrderStatusRequest request);
    OrderDto confirmOrder(Long orderId);
    OrderDto shipOrder(Long orderId, String trackingNumber, String shippingMethod);
    OrderDto deliverOrder(Long orderId);
    OrderDto cancelOrder(Long orderId, String reason);

    // Order Search and Analytics
    CollectionResponse<OrderDto> searchOrders(String searchTerm, Pageable pageable);
    long countOrdersByStatus(OrderStatus status);
    long countOrdersByUserId(Integer userId);

    // Order Validation
    boolean canUpdateOrderStatus(Long orderId, OrderStatus newStatus);
    boolean canCancelOrder(Long orderId);

    // Order Items Management
    OrderDto addItemToOrder(Long orderId, OrderItemDto orderItemDto);
    OrderDto removeItemFromOrder(Long orderId, Long orderItemId);
    OrderDto updateOrderItem(Long orderId, Long orderItemId, OrderItemDto orderItemDto);

    // Payment Integration
    OrderDto updatePaymentStatus(Long orderId, String paymentStatus, String gatewayTransactionId);
    OrderDto confirmPayment(Long orderId, String gatewayTransactionId);
    OrderDto failPayment(Long orderId, String reason);
}