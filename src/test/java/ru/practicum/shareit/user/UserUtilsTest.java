package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.exceptions.ValidationElementException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.utils.UserUtils;

public class UserUtilsTest {

    private UserUtils utils = new UserUtils();

    @Test
    void shouldCheckValidateUserDto() {
        UserDto userDto = UserDto.builder()
                .id(1)
                .name("UserName")
                .email("new@mail.ru")
                .build();
        utils.checkUserValid(userDto);

        userDto.setName(" ");
        Assertions.assertThrows(ValidationElementException.class, () -> utils.checkUserValid(userDto));

        userDto.setName("UserName");
        userDto.setEmail(null);
        Assertions.assertThrows(ValidationElementException.class, () -> utils.checkUserValid(userDto));
    }
}
