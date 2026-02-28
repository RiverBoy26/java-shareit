package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item addItem(Item item);

    Item renewItem(Long itemId);

    Item getItemById(Long itemId);

    List<Item> getAllItems();

    List<Item> getItemsAfterSearch(String text);
}
