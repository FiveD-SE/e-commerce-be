package com.pm.userservice.mapper;

import com.pm.userservice.dto.CredentialDto;
import com.pm.userservice.model.Credential;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = {VerificationTokenMapper.class}
)
public interface CredentialMapper {
    CredentialMapper INSTANCE = Mappers.getMapper(CredentialMapper.class);

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "verificationTokens", target = "verificationTokenDtos")
    CredentialDto toDto(Credential credential);

    @Mapping(source = "userId", target = "user.userId")
    @Mapping(source = "verificationTokenDtos", target = "verificationTokens")
    Credential toEntity(CredentialDto credentialDto);
}
