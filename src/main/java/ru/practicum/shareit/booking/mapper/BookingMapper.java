package ru.practicum.shareit.booking.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.Status;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    private final UserMapper userMapper;

    public Booking toBooking(BookingDto bookingDto, Item item, User booker, Status bookingStatus) {
        Booking booking = new Booking();

        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(bookingStatus);

        return booking;
    }

    public BookingDto toDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        BookingDto bookingDto = new BookingDto();

        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(booking.getStatus());

        if (booking.getItem() != null) {
            bookingDto.setItemId(booking.getItem().getId());
            bookingDto.setItem(ItemMapper.toItemDto(booking.getItem()));
        }

        if (booking.getBooker() != null) {
            bookingDto.setBookerId(booking.getBooker().getId());
            bookingDto.setBooker(userMapper.toDto(booking.getBooker()));
        }

        return bookingDto;
    }
}
