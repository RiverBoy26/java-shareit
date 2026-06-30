package ru.practicum.server.item.service;

import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, Long userId);

    ItemDto renewItem(ItemDto itemDto, Long itemId, Long userId);

    ItemDto getItemById(Long itemId, Long userId);

    List<ItemDto> getAllItems(Long userId);

    List<ItemDto> getItemsAfterSearch(String text);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
