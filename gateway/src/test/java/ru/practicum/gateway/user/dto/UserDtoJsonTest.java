package ru.practicum.gateway.user.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoJsonTest {
    @Autowired
    private JacksonTester<UserDto> json;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void userDto_shouldSerializeToJson() throws Exception {
        UserDto dto = new UserDto(1L, "Artyom", "artyom@test.com");

        var result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Artyom");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("artyom@test.com");
    }

    @Test
    void userDto_shouldDeserializeFromJson() throws Exception {
        String content = """
                {
                  "id": 1,
                  "name": "Artyom",
                  "email": "artyom@test.com"
                }
                """;

        UserDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Artyom");
        assertThat(dto.getEmail()).isEqualTo("artyom@test.com");
    }

    @Test
    void userDto_shouldFailValidationWhenEmailInvalid() {
        UserDto dto = new UserDto(1L, "Artyom", "invalid-email");

        var violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }
}