package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundElementException;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private static int keyId = 0;
    private final HashMap<Integer, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        Integer newId = generateId();
        user.setId(newId);
        users.put(user.getId(), user);
        return get(user.getId());
    }

    @Override
    public User get(Integer userId) {
        if (!isUser(userId)) {
            throw new NotFoundElementException();
        }
        return users.get(userId);
    }

    @Override
    public User update(User user) {
        if (!isUser(user.getId())) {
            throw new NotFoundElementException();
        }
        User oldUser = users.get(user.getId());
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            user.setEmail(oldUser.getEmail());
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(oldUser.getName());
        }
        users.put(user.getId(), user);
        return get(user.getId());
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUser(Integer userId) {
        if (!isUser(userId)) {
            throw new NotFoundElementException();
        }
        users.remove(userId);
    }

    public static int generateId() {
        return ++keyId;
    }

    @Override
    public boolean isUser(Integer userId) {
        return users.containsKey(userId);
    }

    @Override
    public boolean checkEmail(String email) {
        List<String> emails = users.values().stream()
                .map(User::getEmail)
                .collect(Collectors.toList());
        return emails.contains(email);
    }
}
