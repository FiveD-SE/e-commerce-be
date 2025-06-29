package com.pm.userservice.service.impl;

import com.pm.userservice.dto.AddressDto;
import com.pm.userservice.dto.SubscriptionRequest;
import com.pm.userservice.dto.UserDto;
import com.pm.userservice.dto.UserProfileDto;
import com.pm.userservice.dto.response.collection.CollectionResponse;
import com.pm.userservice.exception.wrapper.UserObjectNotFoundException;
import com.pm.userservice.mapper.AddressMapperUtils;
import com.pm.userservice.mapper.UserMapper;
import com.pm.userservice.mapper.UserProfileMapper;
import com.pm.userservice.model.Address;
import com.pm.userservice.model.User;
import com.pm.userservice.repository.AddressRepository;
import com.pm.userservice.repository.UserRepository;
import com.pm.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final UserMapper userMapper;

    @Override
    public CollectionResponse<UserDto> findAll() {
        log.info("Fetching all users");
        List<UserDto> users = userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
        return CollectionResponse.<UserDto>builder()
                .data(users)
                .build();
    }

    @Override
    public UserDto findById(Integer userId) {
        log.info("Fetching user with ID: {}", userId);
        return userRepository.findById(userId)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserObjectNotFoundException(String.format("User with id: %d not found", userId)));
    }

    @Override
    @Transactional
    public UserDto save(UserDto userDto) {
        log.info("Saving new user: {}", userDto.getEmail());
        User user = userMapper.toEntity(userDto);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto) {
        log.info("Updating user: {}", userDto.getEmail());
        User user = userMapper.toEntity(userDto);
        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @Override
    @Transactional
    public UserDto update(Integer userId, UserDto userDto) {
        log.info("Updating user with ID: {}", userId);
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserObjectNotFoundException(String.format("User with id: %d not found", userId)));
        User updatedUser = userRepository.save(existingUser);
        return userMapper.toDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteById(Integer userId) {
        log.info("Deleting user with ID: {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new UserObjectNotFoundException(String.format("User with id: %d not found", userId));
        }
        userRepository.deleteById(userId);
    }

    @Override
    public UserDto findByUsername(String username) {
        log.info("Fetching user with username: {}", username);
        return userRepository.findByCredentialUsername(username)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserObjectNotFoundException(String.format("User with username: %s not found", username)));
    }

    // Profile management
    @Override
    @Transactional(readOnly = true)
    public UserProfileDto getUserProfile(Integer userId) {
        log.info("Fetching profile for user ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserObjectNotFoundException(String.format("User with id: %d not found", userId)));
        return UserProfileMapper.map(user);
    }

    @Override
    @Transactional
    public UserProfileDto updateUserProfile(Integer userId, UserProfileDto userProfileDto) {
        log.info("Updating profile for user ID: {}", userId);
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserObjectNotFoundException(String.format("User with id: %d not found", userId)));
        
        // Update user fields
        existingUser.setFirstName(userProfileDto.getFirstName());
        existingUser.setLastName(userProfileDto.getLastName());
        existingUser.setImageUrl(userProfileDto.getImageUrl());
        existingUser.setEmail(userProfileDto.getEmail());
        existingUser.setPhone(userProfileDto.getPhone());
        existingUser.setEmailSubscription(userProfileDto.getEmailSubscription());
        existingUser.setSmsSubscription(userProfileDto.getSmsSubscription());
        existingUser.setMarketingConsent(userProfileDto.getMarketingConsent());
        existingUser.setNewsletterSubscription(userProfileDto.getNewsletterSubscription());
        
        User updatedUser = userRepository.save(existingUser);
        return UserProfileMapper.map(updatedUser);
    }

    // Address management
    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<AddressDto> getUserAddresses(Integer userId) {
        log.info("Fetching addresses for user ID: {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new UserObjectNotFoundException(String.format("User with id: %d not found", userId));
        }
        
        List<Address> addresses = addressRepository.findByUserUserId(userId);
        List<AddressDto> addressDtos = addresses.stream()
                .map(AddressMapperUtils::map)
                .toList();
        
        return CollectionResponse.<AddressDto>builder()
                .data(addressDtos)
                .totalElements(addressDtos.size())
                .build();
    }

    @Override
    @Transactional
    public AddressDto addUserAddress(Integer userId, AddressDto addressDto) {
        log.info("Adding address for user ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserObjectNotFoundException(String.format("User with id: %d not found", userId)));
        
        Address address = AddressMapperUtils.map(addressDto);
        address.setUser(user);
        Address savedAddress = addressRepository.save(address);
        
        return AddressMapperUtils.map(savedAddress);
    }

    // Subscription management
    @Override
    @Transactional
    public UserDto updateSubscriptions(Integer userId, SubscriptionRequest subscriptionRequest) {
        log.info("Updating subscriptions for user ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserObjectNotFoundException(String.format("User with id: %d not found", userId)));
        
        if (subscriptionRequest.getEmailSubscription() != null) {
            user.setEmailSubscription(subscriptionRequest.getEmailSubscription());
        }
        if (subscriptionRequest.getSmsSubscription() != null) {
            user.setSmsSubscription(subscriptionRequest.getSmsSubscription());
        }
        if (subscriptionRequest.getMarketingConsent() != null) {
            user.setMarketingConsent(subscriptionRequest.getMarketingConsent());
        }
        if (subscriptionRequest.getNewsletterSubscription() != null) {
            user.setNewsletterSubscription(subscriptionRequest.getNewsletterSubscription());
        }
        
        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @Override
    @Transactional
    public UserDto subscribeToEmail(Integer userId) {
        log.info("Subscribing user {} to email notifications", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserObjectNotFoundException(String.format("User with id: %d not found", userId)));
        
        user.setEmailSubscription(true);
        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @Override
    @Transactional
    public UserDto unsubscribeFromEmail(Integer userId) {
        log.info("Unsubscribing user {} from email notifications", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserObjectNotFoundException(String.format("User with id: %d not found", userId)));
        
        user.setEmailSubscription(false);
        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @Override
    @Transactional
    public UserDto subscribeToSms(Integer userId) {
        log.info("Subscribing user {} to SMS notifications", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserObjectNotFoundException(String.format("User with id: %d not found", userId)));
        
        user.setSmsSubscription(true);
        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @Override
    @Transactional
    public UserDto unsubscribeFromSms(Integer userId) {
        log.info("Unsubscribing user {} from SMS notifications", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserObjectNotFoundException(String.format("User with id: %d not found", userId)));
        
        user.setSmsSubscription(false);
        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }
}
