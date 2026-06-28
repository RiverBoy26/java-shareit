package ru.practicum.server.item.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.server.booking.entity.Status;
import ru.practicum.server.booking.repository.BookingRepository;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.exception.NullValueException;
import ru.practicum.server.item.dao.ItemStorage;
import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.entity.Comment;
import ru.practicum.server.item.mapper.ItemMapper;
import ru.practicum.server.item.entity.Item;
import ru.practicum.server.item.repository.CommentRepository;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.request.entity.ItemRequest;
import ru.practicum.server.request.repository.ItemRequestRepository;
import ru.practicum.server.user.dao.UserStorage;
import ru.practicum.server.user.entity.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        validateNewItem(itemDto);

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);

        if (itemDto.getRequestId() != null) {
            ItemRequest request = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос вещи не найден"));
            item.setRequest(request);
        }

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    public ItemDto renewItem(ItemDto itemDto, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем");
        }

        if (itemDto.getName() != null) {
            if (itemDto.getName().isBlank()) {
                throw new NullValueException("Название не может быть пустым");
            }
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            if (itemDto.getDescription().isBlank()) {
                throw new NullValueException("Описание не может быть пустым");
            }
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        ItemDto dto = ItemMapper.toItemDto(item);

        dto.setComments(commentRepository.findAllByItemIdWithAuthor(item.getId()).stream()
                .map(ItemMapper::toCommentDto)
                .toList());

        dto.setLastBooking(null);
        dto.setNextBooking(null);

        boolean isOwner = item.getOwner().getId().equals(userId);

        if (isOwner) {
            LocalDateTime now = LocalDateTime.now();

            dto.setLastBooking(bookingRepository
                    .findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(
                            item.getId(), Status.APPROVED, now)
                    .map(ItemMapper::toBookingShortDto)
                    .orElse(null));

            dto.setNextBooking(bookingRepository
                    .findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                            item.getId(), Status.APPROVED, now)
                    .map(ItemMapper::toBookingShortDto)
                    .orElse(null));
        }

        return dto;
    }

    @Override
    public List<ItemDto> getAllItems(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return itemRepository.findAllByOwnerIdOrderByIdAsc(userId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> getItemsAfterSearch(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        if (commentDto == null || commentDto.getText() == null || commentDto.getText().isBlank()) {
            throw new IllegalArgumentException("Текст комментария не может быть пустым");
        }

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        boolean hasCompletedApprovedBooking = bookingRepository
                .existsByBookerIdAndItemIdAndStatusAndEndBefore(
                        userId,
                        itemId,
                        Status.APPROVED,
                        LocalDateTime.now()
                );

        if (!hasCompletedApprovedBooking) {
            throw new IllegalArgumentException(
                    "Комментарий может оставить только пользователь, который арендовал вещь и уже завершил аренду"
            );
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);

        CommentDto result = new CommentDto();
        result.setId(savedComment.getId());
        result.setText(savedComment.getText());
        result.setAuthorName(savedComment.getAuthor().getName());
        result.setCreated(savedComment.getCreated());

        return result;
    }

    private void validateNewItem(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new NullValueException("Название не может быть пустым");
        }

        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new NullValueException("Описание не может быть пустым");
        }

        if (itemDto.getAvailable() == null) {
            throw new NullValueException("Статус доступности не может быть пустым");
        }
    }
}
