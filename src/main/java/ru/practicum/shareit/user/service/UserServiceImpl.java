package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DuplicatedException;
import ru.practicum.shareit.exceptions.ValidationElementException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final UserMapper userMapper;

    @Override
    public UserDTO create(UserDTO userDTO) {
        checkUserValid(userDTO);
        User user = userStorage.create(userMapper.toUserFromDTO(userDTO));
        return userMapper.toUserDTO(user);
    }

    @Override
    public UserDTO get(Integer userId) {
        return userMapper.toUserDTO(userStorage.get(userId));
    }

    @Override
    public UserDTO update(UserDTO userDTO, Integer userId) {
        userDTO.setId(userId);
        if (userDTO.getEmail() != null) {
            if (!userDTO.getEmail().equals(get(userId).getEmail())) {
                checkEmail(userDTO);
            }
        }
        User user = userStorage.update(userMapper.toUserFromDTO(userDTO));
        return userMapper.toUserDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userStorage.getAllUsers().stream()
                .map(userMapper::toUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Integer userId) {
        userStorage.deleteUser(userId);
    }

    private void checkUserValid(UserDTO userDTO) {
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

    private void checkEmail(UserDTO userDTO) {
        if (userStorage.checkEmail(userDTO.getEmail())) {
            throw new DuplicatedException("Почта уже используется");
        }
    }
}
