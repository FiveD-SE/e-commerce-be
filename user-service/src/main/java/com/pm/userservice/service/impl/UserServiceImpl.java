package com.pm.userservice.service.impl;

import com.pm.userservice.dto.UserDto;
import com.pm.userservice.dto.response.collection.CollectionResponse;
import com.pm.userservice.exception.wrapper.UserObjectNotFoundException;
import com.pm.userservice.mapper.UserMapper;
import com.pm.userservice.model.User;
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
}
