package com.pm.orderservice.service.impl;

import com.pm.orderservice.dto.CreateOrderRequest;
import com.pm.orderservice.dto.OrderDto;
import com.pm.orderservice.dto.OrderItemDto;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Set;
import java.util.UUID;
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
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public OrderDto createOrder(CreateOrderRequest request) {
        // Fetch cart from cart-service
        String cartServiceUrl = "http://cart-service:8300/api/carts/user/" + request.getUserId();
        var cartResponse = restTemplate.getForObject(cartServiceUrl, Object.class); // Replace Object with CartDto if shared
        // TODO: Map cartResponse to order items (simulate for now)
        // In production, use a shared DTO module or OpenAPI generator
        Set<OrderItem> orderItems = Set.of(); // TODO: Map from cartResponse
        double totalAmount = 0.0; // TODO: Calculate from cartResponse

        Order order = Order.builder()
                .userId(request.getUserId())
                .address(request.getAddress())
                .status(OrderStatus.PENDING)
                .totalAmount(totalAmount)
                .orderItems(orderItems)
                .build();
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDTO(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return orderMapper.toDTO(order);
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<OrderDto> getOrdersByUserId(Integer userId) {
        List<OrderDto> orders = orderRepository.findByUserId(userId).stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
        return CollectionResponse.<OrderDto>builder()
                .data(orders)
                .totalElements(orders.size())
                .build();
    }

    @Override
    public OrderDto updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toDTO(updatedOrder);
    }
} 