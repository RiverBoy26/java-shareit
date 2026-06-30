package ru.practicum.server.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.exception.ValueAlreadyExistException;
import ru.practicum.server.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Sql(
        statements = {
                "DELETE FROM comments",
                "DELETE FROM bookings",
                "DELETE FROM items",
                "DELETE FROM requests",
                "DELETE FROM users"
        },
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class UserServiceImplIntegrationTest {
    @Autowired
    private UserService userService;

    @Test
    void addUser_shouldSaveUser() {
        UserDto userDto = new UserDto(null, "Artyom", "artyom@test.com");

        UserDto saved = userService.addUser(userDto);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Artyom");
        assertThat(saved.getEmail()).isEqualTo("artyom@test.com");
    }

    @Test
    void addUser_shouldThrowConflictWhenEmailAlreadyExists() {
        userService.addUser(new UserDto(null, "User One", "same@test.com"));

        assertThatThrownBy(() -> userService.addUser(new UserDto(null, "User Two", "same@test.com")))
                .isInstanceOf(ValueAlreadyExistException.class);
    }

    @Test
    void renewUser_shouldUpdateOnlyPassedFields() {
        UserDto saved = userService.addUser(new UserDto(null, "Old name", "old@test.com"));

        UserDto patch = new UserDto();
        patch.setName("New name");

        UserDto updated = userService.renewUser(saved.getId(), patch);

        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getName()).isEqualTo("New name");
        assertThat(updated.getEmail()).isEqualTo("old@test.com");
    }

    @Test
    void getUserById_shouldReturnSavedUser() {
        UserDto saved = userService.addUser(new UserDto(null, "Artyom", "artyom@test.com"));

        UserDto found = userService.getUserById(saved.getId());

        assertThat(found).isEqualTo(saved);
    }

    @Test
    void getUserById_shouldThrowNotFoundWhenUserDoesNotExist() {
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void deleteUser_shouldRemoveUser() {
        UserDto saved = userService.addUser(new UserDto(null, "Artyom", "artyom@test.com"));

        userService.deleteUser(saved.getId());

        assertThatThrownBy(() -> userService.getUserById(saved.getId()))
                .isInstanceOf(NotFoundException.class);
    }
}