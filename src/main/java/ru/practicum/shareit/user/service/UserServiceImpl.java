package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValueAlreadyExistException;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private UserStorage userStorage;

    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);

        if (userStorage.getAllUsers().contains(user)) {
            throw new ValueAlreadyExistException("Данный пользователь уже существует!");
        }

        List<String> emails = userStorage.getAllUsers().stream().map(User::getEmail).toList();

        if (emails.contains(user.getEmail())) {
            throw new ValueAlreadyExistException("Пользователь с данной почтой уже существует!");
        }

        return UserMapper.toUserDto(userStorage.addUser(user));
    }

    public UserDto renewUser(Long userId, UserDto userDto) {
        User user = UserMapper.toUser(userDto);

        if (!userStorage.getAllUsers().stream().map(User::getId).toList().contains(userId)) {
            throw new NotFoundException("Данный пользователь не найден");
        }

        List<String> emails = userStorage.getAllUsers().stream().map(User::getEmail).toList();

        if (emails.contains(user.getEmail())) {
            throw new ValueAlreadyExistException("Пользователь с данной почтой уже существует!");
        }

        return UserMapper.toUserDto(userStorage.renewUser(userId, UserMapper.toUser(userDto)));
    }

    public UserDto getUserById(Long userId) {
        if (!userStorage.getAllUsers().stream().map(User::getId).toList().contains(userId)) {
            throw new NotFoundException("Данный пользователь не найден");
        }

        return UserMapper.toUserDto(userStorage.returnUserById(userId));
    }

    public void deleteUser(Long userId) {
        userStorage.deleteUser(userId);
    }
}
