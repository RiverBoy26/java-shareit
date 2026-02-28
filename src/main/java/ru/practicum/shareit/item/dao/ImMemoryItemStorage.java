package ru.practicum.shareit.item.dao;

import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ImMemoryItemStorage implements ItemStorage {
    private List<Item> items;

    public Item addItem(Item item) {
        if (!items.contains(item)) {
            items.add(item);
        } else {
            throw new RuntimeException("Данный объект уже добавлен!")
        }

        return item;
    }

    public Item renewItem(Long itemId) {

        return new Item();
    }

    public Item getItemById(Long itemId) {

    }

    public List<Item> getAllItems() {
        return items;
    }

    public List<Item> getItemsAfterSearch(String text) {
        return new ArrayList<>();
    }
}
