package ru.practicum.server.booking.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.entity.State;
import ru.practicum.server.booking.entity.Status;
import ru.practicum.server.exception.ForbiddenException;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.exception.UnavailableException;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.service.ItemService;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Sql(
        statements = {
                "DELETE FROM comments",
                "DELETE FROM bookings",
                "DELETE FROM items",
                "DELETE FROM requests",
                "DELETE FROM users"
        },
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class BookingServiceImplIntegrationTest {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Test
    void addBooking_shouldCreateWaitingBooking() {
        UserDto owner = createUser("Owner", "owner@test.com");
        UserDto booker = createUser("Booker", "booker@test.com");
        ItemDto item = itemService.addItem(createItemDto("Дрель", true), owner.getId());

        BookingDto bookingDto = createBookingDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        BookingDto saved = bookingService.addBooking(bookingDto, booker.getId());

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStatus()).isEqualTo(Status.WAITING);
        assertThat(saved.getItem().getId()).isEqualTo(item.getId());
        assertThat(saved.getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void addBooking_shouldThrowWhenItemUnavailable() {
        UserDto owner = createUser("Owner", "owner@test.com");
        UserDto booker = createUser("Booker", "booker@test.com");
        ItemDto item = itemService.addItem(createItemDto("Дрель", false), owner.getId());

        BookingDto bookingDto = createBookingDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        assertThatThrownBy(() -> bookingService.addBooking(bookingDto, booker.getId()))
                .isInstanceOf(UnavailableException.class);
    }

    @Test
    void addBooking_shouldThrowWhenOwnerBooksOwnItem() {
        UserDto owner = createUser("Owner", "owner@test.com");
        ItemDto item = itemService.addItem(createItemDto("Дрель", true), owner.getId());

        BookingDto bookingDto = createBookingDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        assertThatThrownBy(() -> bookingService.addBooking(bookingDto, owner.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void bookingConfirm_shouldApproveBookingByOwner() {
        UserDto owner = createUser("Owner", "owner@test.com");
        UserDto booker = createUser("Booker", "booker@test.com");
        ItemDto item = itemService.addItem(createItemDto("Дрель", true), owner.getId());

        BookingDto booking = bookingService.addBooking(createBookingDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        ), booker.getId());

        BookingDto approved = bookingService.bookingConfirm(booking.getId(), owner.getId(), true);

        assertThat(approved.getStatus()).isEqualTo(Status.APPROVED);
    }

    @Test
    void bookingConfirm_shouldThrowWhenUserIsNotOwner() {
        UserDto owner = createUser("Owner", "owner@test.com");
        UserDto booker = createUser("Booker", "booker@test.com");
        UserDto other = createUser("Other", "other@test.com");
        ItemDto item = itemService.addItem(createItemDto("Дрель", true), owner.getId());

        BookingDto booking = bookingService.addBooking(createBookingDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        ), booker.getId());

        assertThatThrownBy(() -> bookingService.bookingConfirm(booking.getId(), other.getId(), true))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void getBookingById_shouldReturnBookingForBooker() {
        UserDto owner = createUser("Owner", "owner@test.com");
        UserDto booker = createUser("Booker", "booker@test.com");
        ItemDto item = itemService.addItem(createItemDto("Дрель", true), owner.getId());

        BookingDto booking = bookingService.addBooking(createBookingDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        ), booker.getId());

        BookingDto found = bookingService.getBookingById(booking.getId(), booker.getId());

        assertThat(found.getId()).isEqualTo(booking.getId());
    }

    @Test
    void getBookingById_shouldThrowWhenUserIsNotBookerOrOwner() {
        UserDto owner = createUser("Owner", "owner@test.com");
        UserDto booker = createUser("Booker", "booker@test.com");
        UserDto other = createUser("Other", "other@test.com");
        ItemDto item = itemService.addItem(createItemDto("Дрель", true), owner.getId());

        BookingDto booking = bookingService.addBooking(createBookingDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        ), booker.getId());

        assertThatThrownBy(() -> bookingService.getBookingById(booking.getId(), other.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getBookingsByUser_shouldReturnUserBookings() {
        UserDto owner = createUser("Owner", "owner@test.com");
        UserDto booker = createUser("Booker", "booker@test.com");
        ItemDto item = itemService.addItem(createItemDto("Дрель", true), owner.getId());

        bookingService.addBooking(createBookingDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        ), booker.getId());

        List<BookingDto> bookings = bookingService.getBookingsByUser(State.ALL, booker.getId());

        assertThat(bookings).hasSize(1);
    }

    @Test
    void getBookingsByOwner_shouldReturnOwnerBookings() {
        UserDto owner = createUser("Owner", "owner@test.com");
        UserDto booker = createUser("Booker", "booker@test.com");
        ItemDto item = itemService.addItem(createItemDto("Дрель", true), owner.getId());

        bookingService.addBooking(createBookingDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        ), booker.getId());

        List<BookingDto> bookings = bookingService.getBookingsByOwner(State.ALL, owner.getId());

        assertThat(bookings).hasSize(1);
    }

    private UserDto createUser(String name, String email) {
        return userService.addUser(new UserDto(null, name, email));
    }

    private ItemDto createItemDto(String name, Boolean available) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(name);
        itemDto.setDescription("Описание");
        itemDto.setAvailable(available);
        return itemDto;
    }

    private BookingDto createBookingDto(Long itemId, LocalDateTime start, LocalDateTime end) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemId);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        return bookingDto;
    }
}