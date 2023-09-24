package ru.practicum.shareit.user.utils;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ValidationElementException;
import ru.practicum.shareit.user.dto.UserDto;

@Component
public class UserUtils {

    public void checkUserValid(UserDto userDTO) {
        if (userDTO.getName().isBlank() || userDTO.getName() == null) {
            throw new ValidationElementException("Имя отсутствует");
        }
        if (userDTO.getEmail() == null) {
            throw new ValidationElementException("Почта не указана");
        }
    }
}