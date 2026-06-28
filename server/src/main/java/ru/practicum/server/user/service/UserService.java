package ru.practicum.server.user.service;

import ru.practicum.server.user.dto.UserDto;

public interface UserService {
    UserDto addUser(UserDto userDto);

    UserDto renewUser(Long userId, UserDto userDto);

    UserDto getUserById(Long userId);

    void deleteUser(Long userId);
}
