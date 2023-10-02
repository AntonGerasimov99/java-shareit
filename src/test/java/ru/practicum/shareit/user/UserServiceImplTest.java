package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.utils.UserUtils;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserStorage userStorage;
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserUtils utils;
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
    void shouldCreateUser() {
        Mockito.when(userStorage.save(any(User.class))).thenReturn(user);

        UserDto result = userService.create(userDto);

        assertThat(userDto.getId(), equalTo(result.getId()));
        assertThat(userDto.getName(), equalTo(result.getName()));
        assertThat(userDto.getEmail(), equalTo(result.getEmail()));

        Mockito.verify(userStorage, times(1)).save(any(User.class));
    }

    @Test
    void shouldGetUser() {
        Mockito.when(userStorage.findById(userDto.getId())).thenReturn(Optional.ofNullable(UserMapper.toUserFromDTO(userDto)));

        UserDto result = userService.get(userDto.getId());

        assertThat(userDto.getId(), equalTo(result.getId()));
        assertThat(userDto.getName(), equalTo(result.getName()));
        assertThat(userDto.getEmail(), equalTo(result.getEmail()));
    }

    @Test
    void shouldUpdateUser() {
        Mockito.when(userStorage.findById(userDto.getId())).thenReturn(Optional.ofNullable(user));
        Mockito.when(userStorage.save(any(User.class))).thenReturn(user);

        UserDto result = userService.update(userDto, userDto.getId());

        assertThat(userDto.getId(), equalTo(result.getId()));
        assertThat(userDto.getName(), equalTo(result.getName()));
        assertThat(userDto.getEmail(), equalTo(result.getEmail()));

        UserDto emptyUser = UserDto.builder().build();

        UserDto resultForEmptyUser = userService.update(emptyUser, userDto.getId());

        assertThat(userDto.getId(), equalTo(resultForEmptyUser.getId()));
        assertThat(userDto.getName(), equalTo(resultForEmptyUser.getName()));
        assertThat(userDto.getEmail(), equalTo(resultForEmptyUser.getEmail()));
    }

    @Test
    void shouldGetAllUsers() {
        User secondUser = User.builder().build();
        List<User> userList = List.of(user, secondUser);
        Mockito.when(userStorage.findAll()).thenReturn(userList);

        List<UserDto> result = userService.getAllUsers();
        assertThat(2, equalTo(result.size()));
    }

    @Test
    void shouldDeleteUser() {
        userService.deleteUser(1);
    }
}