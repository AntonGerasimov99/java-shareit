package ru.practicum.shareit.booking;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.StatusEnum;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@JsonTest
public class BookingDtoTest {

    @Autowired
    private JacksonTester json;

    @Test
    void testBookingDto() throws IOException {
        ItemDto itemDto = ItemDto.builder()
                .id(1)
                .name("name")
                .description("description")
                .available(true)
                .build();

        UserDto userDto = UserDto.builder()
                .id(1)
                .name("UserName")
                .email("new@mail.ru")
                .build();

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        BookingDto bookingDto = BookingDto.builder()
                .id(1)
                .start(start)
                .end(end)
                .itemId(itemDto.getId())
                .item(itemDto)
                .status(StatusEnum.APPROVED)
                .booker(userDto)
                .build();

        JsonContent<BookingDto> result = json.write(bookingDto);

        Assertions.assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        Assertions.assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        Assertions.assertThat(result).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(1);
        Assertions.assertThat(result).extractingJsonPathNumberValue("$.item.id")
                .isEqualTo(1);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.item.name")
                .isEqualTo("name");
        Assertions.assertThat(result).extractingJsonPathStringValue("$.item.description")
                .isEqualTo("description");
        Assertions.assertThat(result).extractingJsonPathBooleanValue("$.item.available")
                .isEqualTo(true);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.status")
                .isEqualTo(StatusEnum.APPROVED.toString());
        Assertions.assertThat(result).extractingJsonPathNumberValue("$.booker.id")
                .isEqualTo(1);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.booker.name")
                .isEqualTo("UserName");
        Assertions.assertThat(result).extractingJsonPathStringValue("$.booker.email")
                .isEqualTo("new@mail.ru");
    }
}
