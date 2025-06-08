package com.pm.userservice.service.impl;

import com.pm.userservice.dto.CredentialDto;
import com.pm.userservice.dto.response.collection.CollectionResponse;
import com.pm.userservice.exception.wrapper.CredentialNotFoundException;
import com.pm.userservice.exception.wrapper.UserObjectNotFoundException;
import com.pm.userservice.mapper.CredentialMapper;
import com.pm.userservice.model.Credential;
import com.pm.userservice.repository.CredentialRepository;
import com.pm.userservice.service.CredentialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CredentialServiceImpl implements CredentialService {

    private final CredentialRepository credentialRepository;
    private final CredentialMapper credentialMapper;

    @Override
    public CollectionResponse<CredentialDto> findAll() {
        log.info("Fetching all credentials");
        List<CredentialDto> credentials = credentialRepository.findAll().stream()
                .map(credentialMapper::toDto)
                .toList();
        return CollectionResponse.<CredentialDto>builder()
                .data(credentials)
                .build();
    }

    @Override
    public CredentialDto findById(Integer credentialId) {
        log.info("Fetching credential with ID: {}", credentialId);
        return credentialRepository.findById(credentialId)
                .map(credentialMapper::toDto)
                .orElseThrow(() -> new CredentialNotFoundException(String.format("Credential with id: %d not found", credentialId)));
    }

    @Override
    @Transactional
    public CredentialDto save(CredentialDto credentialDto) {
        log.info("Saving new credential: {}", credentialDto.getUsername());
        Credential credential = credentialMapper.toEntity(credentialDto);
        Credential savedCredential = credentialRepository.save(credential);
        return credentialMapper.toDto(savedCredential);
    }

    @Override
    @Transactional
    public CredentialDto update(CredentialDto credentialDto) {
        log.info("Updating credential: {}", credentialDto.getUsername());
        Credential credential = credentialMapper.toEntity(credentialDto);
        Credential updatedCredential = credentialRepository.save(credential);
        return credentialMapper.toDto(updatedCredential);
    }

    @Override
    @Transactional
    public CredentialDto update(Integer credentialId, CredentialDto credentialDto) {
        log.info("Updating credential with ID: {}", credentialId);
        Credential existingCredential = credentialRepository.findById(credentialId)
                .orElseThrow(() -> new CredentialNotFoundException(String.format("Credential with id: %d not found", credentialId)));
        Credential updatedCredential = credentialRepository.save(existingCredential);
        return credentialMapper.toDto(updatedCredential);
    }

    @Override
    @Transactional
    public void deleteById(Integer credentialId) {
        log.info("Deleting credential with ID: {}", credentialId);
        if (!credentialRepository.existsById(credentialId)) {
            throw new CredentialNotFoundException(String.format("Credential with id: %d not found", credentialId));
        }
        credentialRepository.deleteById(credentialId);
    }

    @Override
    public CredentialDto findByUsername(String username) {
        log.info("Fetching credential with username: {}", username);
        return credentialRepository.findByUsername(username)
                .map(credentialMapper::toDto)
                .orElseThrow(() -> new UserObjectNotFoundException(String.format("Credential with username: %s not found", username)));
    }
}
