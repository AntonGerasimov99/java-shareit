package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(UserDto userDTO);

    UserDto get(Integer userId);

    UserDto update(UserDto userDTO, Integer userId);

    List<UserDto> getAllUsers();

    void deleteUser(Integer userId);
}