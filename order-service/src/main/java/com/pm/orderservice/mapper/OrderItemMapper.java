package com.pm.orderservice.mapper;

import com.pm.orderservice.dto.OrderItemDto;
import com.pm.orderservice.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    @Mapping(target = "orderItemId", source = "orderItemId")
    OrderItemDto toDTO(OrderItem orderItem);

    @Mapping(target = "order", ignore = true)
    @Mapping(target = "orderItemId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    OrderItem toEntity(OrderItemDto orderItemDto);
} 