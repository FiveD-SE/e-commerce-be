package com.pm.userservice.service.impl;

import com.pm.userservice.dto.AddressDto;
import com.pm.userservice.dto.response.collection.CollectionResponse;
import com.pm.userservice.exception.wrapper.AddressNotFoundException;
import com.pm.userservice.mapper.AddressMapper;
import com.pm.userservice.model.Address;
import com.pm.userservice.repository.AddressRepository;
import com.pm.userservice.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @Override
    public CollectionResponse<AddressDto> findAll() {
        log.info("Fetching all addresses");
        List<AddressDto> addresses = addressRepository.findAll().stream()
                .map(addressMapper::toDto)
                .toList();
        return CollectionResponse.<AddressDto>builder()
                .data(addresses)
                .build();
    }

    @Override
    public AddressDto findById(Integer addressId) {
        log.info("Fetching address with ID: {}", addressId);
        return addressRepository.findById(addressId)
                .map(addressMapper::toDto)
                .orElseThrow(() -> new AddressNotFoundException(String.format("Address with id: %d not found", addressId)));
    }

    @Override
    @Transactional
    public AddressDto save(AddressDto addressDto) {
        log.info("Saving new address: {}", addressDto.getFullAddress());
        Address address = addressMapper.toEntity(addressDto);
        Address savedAddress = addressRepository.save(address);
        return addressMapper.toDto(savedAddress);
    }

    @Override
    @Transactional
    public AddressDto update(AddressDto addressDto) {
        log.info("Updating address: {}", addressDto.getFullAddress());
        Address address = addressMapper.toEntity(addressDto);
        Address updatedAddress = addressRepository.save(address);
        return addressMapper.toDto(updatedAddress);
    }

    @Override
    @Transactional
    public AddressDto update(Integer addressId, AddressDto addressDto) {
        log.info("Updating address with ID: {}", addressId);
        Address existingAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException(String.format("Address with id: %d not found", addressId)));
        Address updatedAddress = addressRepository.save(existingAddress);
        return addressMapper.toDto(updatedAddress);
    }

    @Override
    @Transactional
    public void deleteById(Integer addressId) {
        log.info("Deleting address with ID: {}", addressId);
        if (!addressRepository.existsById(addressId)) {
            throw new AddressNotFoundException(String.format("Address with id: %d not found", addressId));
        }
        addressRepository.deleteById(addressId);
    }
}
