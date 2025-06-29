package com.pm.userservice.service;

import com.pm.userservice.dto.AddressDto;
import com.pm.userservice.dto.SubscriptionRequest;
import com.pm.userservice.dto.UserDto;
import com.pm.userservice.dto.UserProfileDto;
import com.pm.userservice.dto.response.collection.CollectionResponse;

public interface UserService {

    CollectionResponse<UserDto> findAll();
    UserDto findById(Integer userId);
    UserDto save(UserDto userDto);
    UserDto update(UserDto userDto);
    UserDto update(Integer userId, UserDto userDto);
    void deleteById(Integer userId);
    UserDto findByUsername(String username);

    // Profile management
    UserProfileDto getUserProfile(Integer userId);
    UserProfileDto updateUserProfile(Integer userId, UserProfileDto userProfileDto);

    // Address management  
    CollectionResponse<AddressDto> getUserAddresses(Integer userId);
    AddressDto addUserAddress(Integer userId, AddressDto addressDto);

    // Subscription management
    UserDto updateSubscriptions(Integer userId, SubscriptionRequest subscriptionRequest);
    UserDto subscribeToEmail(Integer userId);
    UserDto unsubscribeFromEmail(Integer userId);
    UserDto subscribeToSms(Integer userId);
    UserDto unsubscribeFromSms(Integer userId);

}
