package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserStorage {

    User create(User user);

    User get(Integer userId);

    User update(User user);

    List<User> getAllUsers();

    void deleteUser(Integer userId);

    boolean isUser(Integer userId);

    boolean checkEmail(String email);
}
