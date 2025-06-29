package com.pm.orderservice.mapper;

import com.pm.orderservice.dto.OrderDto;
import com.pm.orderservice.model.Order;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {

    @Mapping(target = "orderItems", source = "orderItems")
    OrderDto toDTO(Order order);

    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Order toEntity(OrderDto orderDto);

    List<OrderDto> toDTOList(List<Order> orders);

    List<Order> toEntityList(List<OrderDto> orderDtos);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(OrderDto orderDto, @MappingTarget Order order);
}