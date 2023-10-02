package ru.practicum.shareit.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@JsonTest
public class ItemRequestDtoTest {

    @Autowired
    private JacksonTester json;

    @Test
    void testRequestDto() throws IOException {

        LocalDateTime createdDate = LocalDateTime.now();

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

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1)
                .description("description")
                .items(List.of(itemDto))
                .created(createdDate)
                .build();


        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        Assertions.assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("description");
        Assertions.assertThat(result).extractingJsonPathNumberValue("$.items.[0].id")
                .isEqualTo(1);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.items.[0].name")
                .isEqualTo("name");
        Assertions.assertThat(result).extractingJsonPathStringValue("$.items.[0].description")
                .isEqualTo("description");
        Assertions.assertThat(result).extractingJsonPathBooleanValue("$.items.[0].available")
                .isEqualTo(true);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(createdDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}
