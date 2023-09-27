package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundElementException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.utils.UserUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final UserUtils userUtils;

    @Override
    @Transactional
    public UserDto create(UserDto userDTO) {
        userUtils.checkUserValid(userDTO);
        User user = userStorage.save(UserMapper.toUserFromDTO(userDTO));
        return UserMapper.toUserDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto get(Integer userId) {
        return UserMapper.toUserDTO(userStorage.findById(userId)
                        .orElseThrow(() -> new NotFoundElementException("Пользователь не найден")));
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDTO, Integer userId) {
        userDTO.setId(userId);
        User oldUser = userStorage.findById(userDTO.getId())
                        .orElseThrow(() -> new NotFoundElementException("Пользователь не найден"));
        if (userDTO.getEmail() == null || userDTO.getEmail().isBlank()) {
            userDTO.setEmail(oldUser.getEmail());
        }
        if (userDTO.getName() == null || userDTO.getName().isBlank()) {
            userDTO.setName(oldUser.getName());
        }
        User user = userStorage.save(UserMapper.toUserFromDTO(userDTO));
        return UserMapper.toUserDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userStorage.findAll().stream()
                .map(UserMapper::toUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        userStorage.deleteById(userId);
    }
}