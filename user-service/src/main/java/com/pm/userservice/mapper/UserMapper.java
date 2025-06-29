package com.pm.userservice.mapper;

import com.pm.userservice.dto.UserDto;
import com.pm.userservice.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = {AddressMapper.class, WishlistMapper.class}
)
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "addresses", target = "addressDtos")
    @Mapping(source = "wishlists", target = "wishlistDtos")
    @Mapping(source = "credential.credentialId", target = "credentialId")
    UserDto toDto(User user);

    @Mapping(source = "addressDtos", target = "addresses")
    @Mapping(source = "wishlistDtos", target = "wishlists")
    @Mapping(target = "credential", ignore = true)
    User toEntity(UserDto userDto);
}
