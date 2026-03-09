package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NullValueException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Primary
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;

    private final UserStorage userStorage;

    public ItemDto addItem(ItemDto itemDto, Long userId) {
        Item item = ItemMapper.toItem(itemDto);
        checkOwner(item, userId);
        validateItem(item);
        return ItemMapper.toItemDto(itemStorage.addItem(item));
    }

    public ItemDto renewItem(ItemDto itemDto, Long itemId, Long userId) {
        Item item = ItemMapper.toItem(itemDto);
        if (!itemStorage.getAllItems().contains(itemStorage.getItemById(itemId))) {
            throw new NotFoundException("Объект " + itemStorage.getItemById(item.getId()) + " не найден!");
        }
        validateUserId(userId);

        Item oldItem = itemStorage.getItemById(itemId);

        itemOwnerNameDescAvailValidator(item, oldItem, userId);

        return ItemMapper.toItemDto(itemStorage.renewItem(oldItem));

    }

    public ItemDto getItemById(Long itemId) {
        if (!itemStorage.getAllItems().stream().map(Item::getId).toList().contains(itemId)) {
            throw new NotFoundException("Объект не найден!");
        }

        Item item = itemStorage.getItemById(itemId);

        return ItemMapper.toItemDto(item);
    }

    public List<ItemDto> getAllItems(Long userId) {
        return itemStorage.getAllItems().stream()
                .filter(item -> Objects.equals(item.getOwner(), userId))
                .map(ItemMapper::toItemDto)
                .toList();
    }

    public List<ItemDto> getItemsAfterSearch(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        return itemStorage.getItemsAfterSearch(text).stream().map(ItemMapper::toItemDto).toList();
    }

    private void validateItem(Item item) {
        if (item.getName().isEmpty() || item.getName().isBlank()) {
            throw new NullValueException("Название не может быть пустым!");
        }
        if (item.getDescription().isEmpty() || item.getDescription().isBlank()) {
            throw new NullValueException("Описание не может быть пустым!");
        }

        if (item.getAvailable() == null) {
            throw new NullValueException("Статус не может быть пустым!");
        }
    }

    private void checkOwner(Item item, Long userId) {
        List<Long> usersIds = userStorage.getAllUsers().stream().map(User::getId).toList();
        if (!usersIds.contains(userId)) {
            throw new NotFoundException("Пользователь " + userId + " не найден!");
        }
        item.setOwner(userId);
    }

    private void itemOwnerNameDescAvailValidator(Item item, Item oldItem, long userId) {
        if (oldItem.getOwner() != userId) {
            throw new NotFoundException("Пользователь не является владельцем!");
        }
        if (item.getName() != null && !item.getName().isEmpty()) {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isEmpty()) {
            oldItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }
    }

    private void validateUserId(Long userId) {
        if (!userStorage.getAllUsers().contains(userStorage.returnUserById(userId))) {
            throw new NotFoundException("Пользователь " + userId + " не найден!");
        }
    }
}
