package com.pm.userservice.mapper;

import com.pm.userservice.dto.AddressDto;
import com.pm.userservice.model.Address;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface AddressMapper {
    AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

    @Mapping(source = "user.userId", target = "userId")
    AddressDto toDto(Address address);

    @Mapping(source = "userId", target = "user.userId")
    Address toEntity(AddressDto dto);
}
