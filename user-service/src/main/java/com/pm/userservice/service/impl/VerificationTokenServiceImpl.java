package com.pm.userservice.service.impl;

import com.pm.userservice.dto.VerificationTokenDto;
import com.pm.userservice.dto.response.collection.CollectionResponse;
import com.pm.userservice.exception.wrapper.VerificationTokenNotFoundException;
import com.pm.userservice.mapper.VerificationTokenMapper;
import com.pm.userservice.model.VerificationToken;
import com.pm.userservice.repository.VerificationTokenRepository;
import com.pm.userservice.service.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class VerificationTokenServiceImpl implements VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;
    private final VerificationTokenMapper verificationTokenMapper;

    @Override
    public CollectionResponse<VerificationTokenDto> findAll() {
        log.info("Fetching all verification tokens");
        List<VerificationTokenDto> tokens = verificationTokenRepository.findAll().stream()
                .map(verificationTokenMapper::toDto)
                .toList();
        return CollectionResponse.<VerificationTokenDto>builder()
                .data(tokens)
                .build();
    }

    @Override
    public VerificationTokenDto findById(Integer verificationTokenId) {
        log.info("Fetching verification token with ID: {}", verificationTokenId);
        return verificationTokenRepository.findById(verificationTokenId)
                .map(verificationTokenMapper::toDto)
                .orElseThrow(() -> new VerificationTokenNotFoundException(String.format("VerificationToken with id: %d not found", verificationTokenId)));
    }

    @Override
    @Transactional
    public VerificationTokenDto save(VerificationTokenDto verificationTokenDto) {
        log.info("Saving new verification token: {}", verificationTokenDto.getToken());
        VerificationToken token = verificationTokenMapper.toEntity(verificationTokenDto);
        VerificationToken savedToken = verificationTokenRepository.save(token);
        return verificationTokenMapper.toDto(savedToken);
    }

    @Override
    @Transactional
    public VerificationTokenDto update(VerificationTokenDto verificationTokenDto) {
        log.info("Updating verification token: {}", verificationTokenDto.getToken());
        VerificationToken token = verificationTokenMapper.toEntity(verificationTokenDto);
        VerificationToken updatedToken = verificationTokenRepository.save(token);
        return verificationTokenMapper.toDto(updatedToken);
    }

    @Override
    @Transactional
    public VerificationTokenDto update(Integer verificationTokenId, VerificationTokenDto verificationTokenDto) {
        log.info("Updating verification token with ID: {}", verificationTokenId);
        VerificationToken existingToken = verificationTokenRepository.findById(verificationTokenId)
                .orElseThrow(() -> new VerificationTokenNotFoundException(String.format("VerificationToken with id: %d not found", verificationTokenId)));
        VerificationToken updatedToken = verificationTokenRepository.save(existingToken);
        return verificationTokenMapper.toDto(updatedToken);
    }

    @Override
    @Transactional
    public void deleteById(Integer verificationTokenId) {
        log.info("Deleting verification token with ID: {}", verificationTokenId);
        if (!verificationTokenRepository.existsById(verificationTokenId)) {
            throw new VerificationTokenNotFoundException(String.format("VerificationToken with id: %d not found", verificationTokenId));
        }
        verificationTokenRepository.deleteById(verificationTokenId);
    }
}
