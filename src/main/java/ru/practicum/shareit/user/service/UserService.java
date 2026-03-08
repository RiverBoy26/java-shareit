package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    UserDto addUser(UserDto userDto);
    UserDto renewUser(Long userId, UserDto userDto);
    UserDto getUserById(Long userId);
    void deleteUser(Long userId);
}
