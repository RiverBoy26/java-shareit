package ru.practicum.gateway.booking;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.gateway.booking.dto.BookingDto;
import ru.practicum.gateway.booking.dto.State;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private static final String HEADER = "X-Sharer-User-Id";

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader(HEADER) Long userId,
                                             @Valid @RequestBody BookingDto bookingDto) {
        validateBooking(bookingDto);
        return bookingClient.addBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> bookingConfirm(@PathVariable Long bookingId,
                                                 @RequestHeader(HEADER) Long userId,
                                                 @RequestParam Boolean approved) {
        return bookingClient.bookingConfirm(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable Long bookingId,
                                                 @RequestHeader(HEADER) Long userId) {
        return bookingClient.getBookingById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByUser(@RequestHeader(HEADER) Long userId,
                                                    @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getBookingsByUser(userId, State.from(state));
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(@RequestHeader(HEADER) Long userId,
                                                     @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getBookingsByOwner(userId, State.from(state));
    }

    private void validateBooking(BookingDto bookingDto) {
        if (bookingDto == null) {
            throw new ValidationException("Тело запроса не может быть пустым");
        }

        if (bookingDto.getItemId() == null) {
            throw new ValidationException("itemId обязателен");
        }

        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new ValidationException("Дата начала и дата окончания бронирования обязательны");
        }

        if (!bookingDto.getEnd().isAfter(bookingDto.getStart())) {
            throw new ValidationException("Дата окончания должна быть позже даты начала");
        }

        LocalDateTime now = LocalDateTime.now();

        if (!bookingDto.getEnd().isAfter(now)) {
            throw new ValidationException("Дата окончания должна быть в будущем");
        }

        if (bookingDto.getStart().isBefore(now.minusSeconds(3))) {
            throw new ValidationException("Дата начала не может быть в прошлом");
        }
    }
}