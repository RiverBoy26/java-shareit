package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValueAlreadyExistException;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.entity.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserStorage userStorage;

    public UserDto addUser(UserDto userDto) {
        User user = userMapper.toEntity(userDto);

        if (userStorage.getAllUsers().contains(user)) {
            throw new ValueAlreadyExistException("Данный пользователь уже существует!");
        }

        List<String> emails = userStorage.getAllUsers().stream().map(User::getEmail).toList();

        if (emails.contains(user.getEmail())) {
            throw new ValueAlreadyExistException("Пользователь с данной почтой уже существует!");
        }

        return userMapper.toDto(userStorage.addUser(user));
    }

    public UserDto renewUser(Long userId, UserDto userDto) {
        User user = userMapper.toEntity(userDto);

        if (!userStorage.getAllUsers().stream().map(User::getId).toList().contains(userId)) {
            throw new NotFoundException("Данный пользователь не найден");
        }

        List<String> emails = userStorage.getAllUsers().stream().map(User::getEmail).toList();

        if (emails.contains(user.getEmail())) {
            throw new ValueAlreadyExistException("Пользователь с данной почтой уже существует!");
        }

        return userMapper.toDto(userStorage.renewUser(userId, userMapper.toEntity(userDto)));
    }

    public UserDto getUserById(Long userId) {
        if (!userStorage.getAllUsers().stream().map(User::getId).toList().contains(userId)) {
            throw new NotFoundException("Данный пользователь не найден");
        }

        return userMapper.toDto(userStorage.returnUserById(userId));
    }

    public void deleteUser(Long userId) {
        userStorage.deleteUser(userId);
    }
}
