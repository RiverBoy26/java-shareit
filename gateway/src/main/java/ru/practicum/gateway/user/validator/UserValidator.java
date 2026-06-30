package ru.practicum.gateway.user.validator;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;
import ru.practicum.gateway.user.dto.UserDto;

@Component
public class UserValidator {
    public void validatePatch(UserDto userDto) {
        if (userDto.getEmail() != null && !userDto.getEmail().contains("@")) {
            throw new ValidationException("Некорректный email");
        }
    }
}