package com.pm.userservice.service;

import com.pm.userservice.dto.CredentialDto;
import com.pm.userservice.dto.response.collection.CollectionResponse;

public interface CredentialService {

    CollectionResponse<CredentialDto> findAll();
    CredentialDto findById(Integer credentialId);
    CredentialDto save(CredentialDto credentialDto);
    CredentialDto update(CredentialDto credentialDto);
    CredentialDto update(Integer credentialId, CredentialDto credentialDto);
    void deleteById(Integer credentialId);
    CredentialDto findByUsername(String username);
}
