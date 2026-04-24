package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemStorage {
    Item addItem(Item item);

    Item renewItem(Item item);

    Item getItemById(Long itemId);

    Collection<Item> getAllItems();

    List<Item> getItemsAfterSearch(String text);
}
