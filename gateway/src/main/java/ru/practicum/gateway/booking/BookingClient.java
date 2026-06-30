package ru.practicum.gateway.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.gateway.booking.dto.BookingDto;
import ru.practicum.gateway.booking.dto.State;
import ru.practicum.gateway.client.BaseClient;

@Component
public class BookingClient extends BaseClient {
    private final String serverUrl;

    public BookingClient(@Value("${shareit-server.url}") String serverUrl,
                         RestTemplateBuilder builder) {
        super(builder);
        this.serverUrl = serverUrl + "/bookings";
    }

    public ResponseEntity<Object> addBooking(BookingDto bookingDto, Long userId) {
        return post(serverUrl, userId, bookingDto);
    }

    public ResponseEntity<Object> bookingConfirm(Long bookingId,
                                                 Long userId,
                                                 Boolean approved) {
        return patch(
                serverUrl + "/" + bookingId + "?approved=" + approved,
                userId
        );
    }

    public ResponseEntity<Object> getBookingById(Long bookingId, Long userId) {
        return get(serverUrl + "/" + bookingId, userId);
    }

    public ResponseEntity<Object> getBookingsByUser(Long userId, State state) {
        return get(serverUrl + "?state=" + state, userId);
    }

    public ResponseEntity<Object> getBookingsByOwner(Long userId, State state) {
        return get(serverUrl + "/owner?state=" + state, userId);
    }
}
