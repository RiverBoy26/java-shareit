package ru.practicum.gateway.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.gateway.item.dto.ItemDto;
import ru.practicum.gateway.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private Long id;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;

    @NotNull
    private Long itemId;

    private Long bookerId;
    private Status status;

    private UserDto booker;
    private ItemDto item;
}
