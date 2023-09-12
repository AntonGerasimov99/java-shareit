package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDTO) {
        return userService.create(userDTO);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable("userId") Integer userId) {
        return userService.get(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable("userId") Integer userId,
                              @RequestBody UserDto userDTO) {
        return userService.update(userDTO, userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable("userId") Integer userId) {
        userService.deleteUser(userId);
    }
}
