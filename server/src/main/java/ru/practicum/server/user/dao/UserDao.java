package ru.practicum.server.user.dao;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.user.entity.User;
import ru.practicum.server.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@Primary
@RequiredArgsConstructor
public class UserDao implements UserStorage {
    private final UserRepository userRepository;

    @Transactional
    public User returnUserById(Long id) {
        return userRepository.getUserById(id);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public Collection<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User addUser(User user) {
        userRepository.save(user);
        return user;
    }

    @Override
    @Transactional
    public User renewUser(Long userId, User user) {
        User currentUser = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        if (user.getName() != null) {
            currentUser.setName(user.getName());
        }

        if (user.getEmail() != null) {
            currentUser.setEmail(user.getEmail());
        }
        userRepository.save(currentUser);
        return currentUser;
    }
}
