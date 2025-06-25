package com.pm.orderservice.mapper;

import com.pm.orderservice.dto.OrderDto;
import com.pm.orderservice.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {
    @Mapping(target = "orderItems", source = "orderItems")
    OrderDto toDTO(Order order);

    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Order toEntity(OrderDto orderDto);
} 