package ru.practicum.server.booking.service;

import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.entity.State;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(BookingDto bookingDto, Long userId);

    BookingDto bookingConfirm(Long bookingId, Long userId, Boolean approved);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getBookingsByUser(State state, Long userId);

    List<BookingDto> getBookingsByOwner(State state, Long userId);
}