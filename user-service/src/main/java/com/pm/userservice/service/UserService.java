package com.pm.userservice.service;

import com.pm.userservice.dto.UserDto;
import com.pm.userservice.dto.response.collection.CollectionResponse;

public interface UserService {

    CollectionResponse<UserDto> findAll();
    UserDto findById(Integer userId);
    UserDto save(UserDto userDto);
    UserDto update(UserDto userDto);
    UserDto update(Integer userId, UserDto userDto);
    void deleteById(Integer userId);
    UserDto findByUsername(String username);
}
