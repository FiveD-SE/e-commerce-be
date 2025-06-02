package com.pm.userservice.service;

import com.pm.userservice.dto.AddressDto;
import com.pm.userservice.dto.response.collection.CollectionResponse;

public interface AddressService {

    CollectionResponse<AddressDto> findAll();
    AddressDto findById(Integer addressId);
    AddressDto save(AddressDto addressDto);
    AddressDto update(AddressDto addressDto);
    AddressDto update(Integer addressId, AddressDto addressDto);
    void deleteById(Integer addressId);
}
