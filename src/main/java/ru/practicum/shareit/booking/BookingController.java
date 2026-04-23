package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.Status;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Booking> addBooking(@RequestBody BookingDto bookingDto,
                                              @RequestHeader(HEADER) Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.addBooking(bookingDto, userId));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Booking> bookingConfirm(@PathVariable Long bookingId,
                                                  @RequestHeader(HEADER) Long userId,
                                                  @RequestParam Boolean approved) {
        return ResponseEntity.ok().body(bookingService.bookingConfirm(bookingId, userId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long bookingId,
                                                  @RequestHeader(HEADER) Long userId) {
        return ResponseEntity.ok().body(bookingService.getBookingById(bookingId, userId));
    }

    @GetMapping("/owner")
    public List<Booking> getBookingsByOwner(@RequestHeader(HEADER) Long userId,
                                               @RequestParam(defaultValue = "ALL") Status state) {
        return bookingService.getBookingsByUser(state, userId);
    }


}
