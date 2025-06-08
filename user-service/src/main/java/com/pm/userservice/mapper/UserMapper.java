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
        uses = {AddressMapper.class}
)
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "addresses", target = "addressDtos")
    @Mapping(source = "credential.credentialId", target = "credentialId")
    UserDto toDto(User user);

    @Mapping(source = "addressDtos", target = "addresses")
    @Mapping(source = "credentialId", target = "credential.credentialId")
    User toEntity(UserDto userDto);
}
