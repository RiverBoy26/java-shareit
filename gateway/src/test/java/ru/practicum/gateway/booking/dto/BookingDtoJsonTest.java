package ru.practicum.gateway.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void bookingDto_shouldSerializeDatesInIsoFormat() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.of(2026, 6, 23, 10, 30));
        dto.setEnd(LocalDateTime.of(2026, 6, 24, 11, 45));

        var result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2026-06-23T10:30:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2026-06-24T11:45:00");
    }

    @Test
    void bookingDto_shouldDeserializeDatesFromJson() throws Exception {
        String content = """
                {
                  "itemId": 1,
                  "start": "2026-06-23T10:30:00",
                  "end": "2026-06-24T11:45:00"
                }
                """;

        BookingDto dto = json.parseObject(content);

        assertThat(dto.getItemId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2026, 6, 23, 10, 30));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2026, 6, 24, 11, 45));
    }
}