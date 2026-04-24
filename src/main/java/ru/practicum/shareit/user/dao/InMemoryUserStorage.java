package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.entity.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long maxId = 0L;

    public User returnUserById(Long id) {
        return users.get(id);
    }

    public User addUser(User user) {
        maxId += 1;
        user.setId(maxId);
        users.put(user.getId(), user);
        return user;
    }

    public User renewUser(Long userId, User user) {

        user.setId(userId);
        users.put(userId, user);
        return user;
    }

    public void deleteUser(Long userId) {
        users.remove(userId);
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }
}
