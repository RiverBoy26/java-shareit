package ru.practicum.server.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.entity.State;
import ru.practicum.server.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private static final String HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestHeader(HEADER) Long userId,
                                         @RequestBody BookingDto bookingDto) {
        return bookingService.addBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto bookingConfirm(@PathVariable Long bookingId,
                                             @RequestHeader(HEADER) Long userId,
                                             @RequestParam Boolean approved) {
        return bookingService.bookingConfirm(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Long bookingId,
                                             @RequestHeader(HEADER) Long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookingsByUser(@RequestHeader(HEADER) Long userId,
                                                      @RequestParam(defaultValue = "ALL") State state) {
        return bookingService.getBookingsByUser(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwner(@RequestHeader(HEADER) Long userId,
                                                       @RequestParam(defaultValue = "ALL") State state) {
        return bookingService.getBookingsByOwner(state, userId);
    }
}