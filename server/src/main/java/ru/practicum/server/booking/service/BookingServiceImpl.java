package ru.practicum.server.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.entity.Booking;
import ru.practicum.server.booking.entity.State;
import ru.practicum.server.booking.entity.Status;
import ru.practicum.server.booking.mapper.BookingMapper;
import ru.practicum.server.booking.repository.BookingRepository;
import ru.practicum.server.exception.ForbiddenException;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.exception.UnavailableException;
import ru.practicum.server.item.entity.Item;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.user.entity.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto addBooking(BookingDto bookingDto, Long userId) {
        LocalDateTime now = LocalDateTime.now();

        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new IllegalArgumentException("Дата начала и дата окончания бронирования обязательны");
        }

        if (!bookingDto.getEnd().isAfter(bookingDto.getStart())) {
            throw new IllegalArgumentException("Дата окончания должна быть позже даты начала");
        }

        if (!bookingDto.getEnd().isAfter(now)) {
            throw new IllegalArgumentException("Дата окончания должна быть в будущем");
        }

        if (bookingDto.getStart().isBefore(now.minusSeconds(3))) {
            throw new IllegalArgumentException("Дата начала не может быть в прошлом");
        }

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (!item.getAvailable()) {
            throw new UnavailableException("Вещь недоступна для бронирования");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("Владелец не может бронировать свою вещь");
        }

        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);

        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto bookingConfirm(Long bookingId, Long userId, Boolean approved) {
        Booking booking = bookingRepository.findByIdWithItemOwnerAndBooker(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Подтверждать бронирование может только владелец вещи");
        }

        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new IllegalArgumentException("Бронирование уже подтверждено или отклонено");
        }

        booking.setStatus(Boolean.TRUE.equals(approved) ? Status.APPROVED : Status.REJECTED);

        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findByIdWithItemOwnerAndBooker(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        Long ownerId = booking.getItem().getOwner().getId();
        Long bookerId = booking.getBooker().getId();

        if (!ownerId.equals(userId) && !bookerId.equals(userId)) {
            throw new NotFoundException("Пользователь не имеет доступа к бронированию");
        }

        return bookingMapper.toDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByUser(State state, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
            case CURRENT -> bookingRepository.findAllCurrentByBookerId(userId, now);
            case PAST -> bookingRepository.findAllPastByBookerId(userId, now);
            case FUTURE -> bookingRepository.findAllFutureByBookerId(userId, now);
            case WAITING -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
            case REJECTED -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
        };

        return bookings.stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByOwner(State state, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
            case CURRENT -> bookingRepository.findAllCurrentByOwnerId(userId, now);
            case PAST -> bookingRepository.findAllPastByOwnerId(userId, now);
            case FUTURE -> bookingRepository.findAllFutureByOwnerId(userId, now);
            case WAITING -> bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
            case REJECTED -> bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
        };

        return bookings.stream()
                .map(bookingMapper::toDto)
                .toList();
    }
}