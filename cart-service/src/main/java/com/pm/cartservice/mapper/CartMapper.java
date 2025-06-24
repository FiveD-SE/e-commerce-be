package com.pm.cartservice.mapper;

import com.pm.cartservice.dto.CartDto;
import com.pm.cartservice.model.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CartItemMapper.class})
public interface CartMapper {
    
    @Mapping(target = "cartItems", source = "cartItems")
    CartDto toDTO(Cart cart);
    
    @Mapping(target = "cartItems", ignore = true)
    @Mapping(target = "cartId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Cart toEntity(CartDto cartDto);
} 