package ru.practicum.gateway.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoJsonTest {
    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void commentDto_shouldSerializeCreatedInIsoFormat() throws Exception {
        CommentDto dto = new CommentDto();
        dto.setId(1L);
        dto.setText("Отличная вещь");
        dto.setAuthorName("Artyom");
        dto.setCreated(LocalDateTime.of(2026, 6, 23, 20, 39, 44));

        var result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Отличная вещь");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Artyom");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2026-06-23T20:39:44");
    }

    @Test
    void commentDto_shouldDeserializeCreatedFromJson() throws Exception {
        String content = """
                {
                  "id": 1,
                  "text": "Отличная вещь",
                  "authorName": "Artyom",
                  "created": "2026-06-23T20:39:44"
                }
                """;

        CommentDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getText()).isEqualTo("Отличная вещь");
        assertThat(dto.getAuthorName()).isEqualTo("Artyom");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2026, 6, 23, 20, 39, 44));
    }
}