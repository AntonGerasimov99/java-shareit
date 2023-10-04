package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @MockBean
    private UserService service;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    private UserDto userDto;
    private User user;

    @BeforeEach
    void beforeEach() {
        userDto = UserDto.builder()
                .id(1)
                .name("name")
                .email("new@mail.ru")
                .build();

        user = User.builder()
                .id(1)
                .name("name")
                .email("new@mail.ru")
                .build();
    }

    @Test
    void shouldCreateUser() throws Exception {
        Mockito.when(service.create(userDto)).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("name")))
                .andExpect(jsonPath("$.email", is("new@mail.ru")));

        verify(service, times(1)).create(userDto);
    }

    @Test
    void shouldGetUser() throws Exception {
        Mockito.when(service.get(userDto.getId())).thenReturn(userDto);

        mockMvc.perform(get("/users/{userId}", 1)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("name")))
                .andExpect(jsonPath("$.email", is("new@mail.ru")));

        verify(service, times(1)).get(userDto.getId());
    }

    @Test
    void shouldUpdateUser() throws Exception {
        Mockito.when(service.update(userDto, userDto.getId())).thenReturn(userDto);

        mockMvc.perform(patch("/users/{userId}", 1)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("name")))
                .andExpect(jsonPath("$.email", is("new@mail.ru")));

        verify(service, times(1)).update(userDto, userDto.getId());
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        UserDto secondUserDto = UserDto.builder()
                .id(2)
                .name("name1")
                .email("new1@mail.ru")
                .build();

        List<UserDto> userList = List.of(userDto, secondUserDto);
        Mockito.when(service.getAllUsers()).thenReturn(userList);

        mockMvc.perform(get("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].name", is("name")))
                .andExpect(jsonPath("$.[0].email", is("new@mail.ru")))
                .andExpect(jsonPath("$.[1].id", is(2)))
                .andExpect(jsonPath("$.[1].name", is("name1")))
                .andExpect(jsonPath("$.[1].email", is("new1@mail.ru")));

        verify(service, times(1)).getAllUsers();
    }

    @Test
    void shouldDelete() throws Exception {
        mockMvc.perform(delete("/users/{userId}", 1))
                .andExpect(status().isOk());

        verify(service, times(1)).deleteUser(1);
    }
}