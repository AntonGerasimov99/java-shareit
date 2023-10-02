package ru.practicum.shareit.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;

@JsonTest
public class UserDtoTest {

    @Autowired
    private JacksonTester json;

    @Test
    void testBookingDto() throws IOException {

        UserDto userDto = UserDto.builder()
                .id(1)
                .name("UserName")
                .email("new@mail.ru")
                .build();

        JsonContent<BookingDto> result = json.write(userDto);

        Assertions.assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo("UserName");
        Assertions.assertThat(result).extractingJsonPathStringValue("$.email")
                .isEqualTo("new@mail.ru");
    }
}
