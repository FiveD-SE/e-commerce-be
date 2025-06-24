package com.pm.cartservice.mapper;

import com.pm.cartservice.dto.CartItemDto;
import com.pm.cartservice.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    
    @Mapping(target = "cartId", source = "cart.cartId")
    CartItemDto toDTO(CartItem cartItem);
    
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "cartItemId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CartItem toEntity(CartItemDto cartItemDto);
} 