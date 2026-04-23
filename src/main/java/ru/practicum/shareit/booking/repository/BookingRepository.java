package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.Status;
import ru.practicum.shareit.user.entity.User;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Booking getBookingById(Long bookingId);


    List<Booking> getBookingsByBookerAndStatus(User booker, Status status);
}
