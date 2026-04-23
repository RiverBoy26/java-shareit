package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.Status;

import java.util.List;

public interface BookingService {
    Booking addBooking(BookingDto booking, Long userId);

    Booking bookingConfirm(Long bookingId, Long userId, Boolean approved);

    Booking getBookingById(Long bookingId, Long userId);

    List<Booking> getBookingsByUser(Status state, Long userId);
}
