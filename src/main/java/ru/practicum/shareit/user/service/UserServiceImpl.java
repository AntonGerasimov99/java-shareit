package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DuplicatedException;
import ru.practicum.shareit.exceptions.ValidationElementException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public UserDto create(UserDto userDTO) {
        checkUserValid(userDTO);
        User user = userStorage.create(UserMapper.toUserFromDTO(userDTO));
        return UserMapper.toUserDTO(user);
    }

    @Override
    public UserDto get(Integer userId) {
        return UserMapper.toUserDTO(userStorage.get(userId));
    }

    @Override
    public UserDto update(UserDto userDTO, Integer userId) {
        userDTO.setId(userId);
        if (userDTO.getEmail() != null) {
            if (!userDTO.getEmail().equals(get(userId).getEmail())) {
                checkEmail(userDTO);
            }
        }
        User user = userStorage.update(UserMapper.toUserFromDTO(userDTO));
        return UserMapper.toUserDTO(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userStorage.getAllUsers().stream()
                .map(UserMapper::toUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Integer userId) {
        userStorage.deleteUser(userId);
    }

    private void checkUserValid(UserDto userDTO) {
        if (userDTO.getName().isBlank() || userDTO.getName() == null) {
            throw new ValidationElementException("Имя отсутствует");
        }
        if (userDTO.getEmail() == null) {
            throw new ValidationElementException("Почта не указана");
        }
        if (userStorage.checkEmail(userDTO.getEmail())) {
            throw new DuplicatedException("Почта уже используется");
        }
    }

    private void checkEmail(UserDto userDTO) {
        if (userStorage.checkEmail(userDTO.getEmail())) {
            throw new DuplicatedException("Почта уже используется");
        }
    }
}
