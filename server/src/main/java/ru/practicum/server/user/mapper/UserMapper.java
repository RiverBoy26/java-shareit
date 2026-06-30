package ru.practicum.server.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserDto userDto);
    UserDto toDto(User user);
}
