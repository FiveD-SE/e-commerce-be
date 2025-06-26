package com.pm.userservice.service;

import com.pm.userservice.dto.VerificationTokenDto;
import com.pm.userservice.dto.response.collection.CollectionResponse;

public interface VerificationTokenService {

    CollectionResponse<VerificationTokenDto> findAll();
    VerificationTokenDto findById(Integer verificationTokenId);
    VerificationTokenDto save(VerificationTokenDto verificationTokenDto);
    VerificationTokenDto update(VerificationTokenDto verificationTokenDto);
    VerificationTokenDto update(Integer verificationTokenId, VerificationTokenDto verificationTokenDto);
    void deleteById(Integer verificationTokenId);
}
