package com.pm.userservice.mapper;

import com.pm.userservice.dto.WishlistDto;
import com.pm.userservice.model.Wishlist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface WishlistMapper {

    @Mapping(source = "user.userId", target = "userId")
    WishlistDto toDto(Wishlist wishlist);

    @Mapping(source = "userId", target = "user.userId")
    Wishlist toEntity(WishlistDto wishlistDto);

} 