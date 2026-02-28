package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.model.User;

import java.sql.ResultSet;

public class UserRowMapper {
    public User mapRow(ResultSet rowSet, int rowNum) {
        User user = new User();
        return user;
    }
}
