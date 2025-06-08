package com.pm.userservice.mapper;

import com.pm.userservice.dto.VerificationTokenDto;
import com.pm.userservice.model.VerificationToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface VerificationTokenMapper {

    VerificationTokenMapper INSTANCE = Mappers.getMapper(VerificationTokenMapper.class);

    @Mapping(source = "credential.credentialId", target = "credentialId")
    VerificationTokenDto toDto(VerificationToken token);

    @Mapping(source = "credentialId", target = "credential.credentialId")
    VerificationToken toEntity(VerificationTokenDto dto);
}
