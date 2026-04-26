package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.entity.User;

import java.util.Collection;

public interface UserStorage {
    User addUser(User user);

    User renewUser(Long userId, User user);

    User returnUserById(Long id);

    void deleteUser(Long userId);

    Collection<User> getAllUsers();
}
