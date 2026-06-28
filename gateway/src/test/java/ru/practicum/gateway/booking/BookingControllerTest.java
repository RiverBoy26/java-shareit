package ru.practicum.gateway.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.gateway.booking.dto.BookingDto;
import ru.practicum.gateway.booking.dto.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {
    private static final String HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingClient bookingClient;

    @Test
    void addBooking_shouldCallClient() throws Exception {
        BookingDto booking = new BookingDto();
        booking.setItemId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));

        when(bookingClient.addBooking(any(BookingDto.class), eq(2L)))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 1,
                        "status", "WAITING"
                )));

        mockMvc.perform(post("/bookings")
                        .header(HEADER, 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(booking)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"));

        verify(bookingClient).addBooking(any(BookingDto.class), eq(2L));
    }

    @Test
    void addBooking_shouldReturnBadRequestWhenItemIdNull() throws Exception {
        BookingDto booking = new BookingDto();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));

        mockMvc.perform(post("/bookings")
                        .header(HEADER, 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(booking)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addBooking_shouldReturnBadRequestWhenEndBeforeStart() throws Exception {
        BookingDto booking = new BookingDto();
        booking.setItemId(1L);
        booking.setStart(LocalDateTime.now().plusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/bookings")
                        .header(HEADER, 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(booking)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bookingConfirm_shouldCallClient() throws Exception {
        when(bookingClient.bookingConfirm(1L, 2L, true))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 1,
                        "status", "APPROVED"
                )));

        mockMvc.perform(patch("/bookings/1")
                        .header(HEADER, 2)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(bookingClient).bookingConfirm(1L, 2L, true);
    }

    @Test
    void getBookingById_shouldCallClient() throws Exception {
        when(bookingClient.getBookingById(1L, 2L))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 1,
                        "status", "WAITING"
                )));

        mockMvc.perform(get("/bookings/1")
                        .header(HEADER, 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(bookingClient).getBookingById(1L, 2L);
    }

    @Test
    void getBookingsByUser_shouldCallClient() throws Exception {
        when(bookingClient.getBookingsByUser(2L, State.ALL))
                .thenReturn(ResponseEntity.ok(List.of(
                        Map.of("id", 1),
                        Map.of("id", 2)
                )));

        mockMvc.perform(get("/bookings")
                        .header(HEADER, 2)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(bookingClient).getBookingsByUser(2L, State.ALL);
    }

    @Test
    void getBookingsByUser_shouldReturnBadRequestWhenStateInvalid() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header(HEADER, 2)
                        .param("state", "UNKNOWN"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingsByOwner_shouldCallClient() throws Exception {
        when(bookingClient.getBookingsByOwner(2L, State.WAITING))
                .thenReturn(ResponseEntity.ok(List.of(
                        Map.of("id", 1)
                )));

        mockMvc.perform(get("/bookings/owner")
                        .header(HEADER, 2)
                        .param("state", "WAITING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(bookingClient).getBookingsByOwner(2L, State.WAITING);
    }

    @Test
    void getBookingsByOwner_shouldReturnBadRequestWhenStateInvalid() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header(HEADER, 2)
                        .param("state", "UNKNOWN"))
                .andExpect(status().isBadRequest());
    }
}