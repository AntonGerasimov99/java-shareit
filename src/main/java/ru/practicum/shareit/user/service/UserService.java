package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDTO;

import java.util.List;

public interface UserService {

    UserDTO create(UserDTO userDTO);

    UserDTO get(Integer userId);

    UserDTO update(UserDTO userDTO, Integer userId);

    List<UserDTO> getAllUsers();

    void deleteUser(Integer userId);
}
