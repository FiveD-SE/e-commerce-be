package com.pm.orderservice.service;

import com.pm.orderservice.dto.CreateOrderRequest;
import com.pm.orderservice.dto.OrderDto;
import com.pm.orderservice.dto.response.collection.CollectionResponse;
import com.pm.orderservice.model.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderDto createOrder(CreateOrderRequest request);
    OrderDto getOrderById(Long orderId);
    CollectionResponse<OrderDto> getOrdersByUserId(Integer userId);
    OrderDto updateOrderStatus(Long orderId, OrderStatus status);
} 