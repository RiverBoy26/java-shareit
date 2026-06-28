package ru.practicum.server.booking.dto;

import lombok.Data;
import ru.practicum.server.booking.entity.Status;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private Long bookerId;
    private Status status;
    private UserDto booker;
    private ItemDto item;
}
