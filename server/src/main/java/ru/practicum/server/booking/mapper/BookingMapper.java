package ru.practicum.server.booking.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.entity.Booking;
import ru.practicum.server.booking.entity.Status;
import ru.practicum.server.item.entity.Item;
import ru.practicum.server.item.mapper.ItemMapper;
import ru.practicum.server.user.entity.User;
import ru.practicum.server.user.mapper.UserMapper;

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
