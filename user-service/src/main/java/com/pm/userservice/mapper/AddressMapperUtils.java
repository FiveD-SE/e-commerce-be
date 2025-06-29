package com.pm.userservice.mapper;

import com.pm.userservice.dto.AddressDto;
import com.pm.userservice.model.Address;

import java.util.Set;
import java.util.stream.Collectors;

public final class AddressMapperUtils {

    private AddressMapperUtils() {
        // Utility class
    }

    public static AddressDto map(final Address address) {
        if (address == null) {
            return null;
        }
        return AddressDto.builder()
                .addressId(address.getAddressId())
                .fullAddress(address.getFullAddress())
                .postalCode(address.getPostalCode())
                .city(address.getCity())
                .userId(address.getUser() != null ? address.getUser().getUserId() : null)
                .build();
    }

    public static Address map(final AddressDto addressDto) {
        if (addressDto == null) {
            return null;
        }
        return Address.builder()
                .addressId(addressDto.getAddressId())
                .fullAddress(addressDto.getFullAddress())
                .postalCode(addressDto.getPostalCode())
                .city(addressDto.getCity())
                .build();
    }

    public static Set<AddressDto> map(final Set<Address> addresses) {
        if (addresses == null) {
            return null;
        }
        return addresses.stream()
                .map(AddressMapperUtils::map)
                .collect(Collectors.toSet());
    }

} 