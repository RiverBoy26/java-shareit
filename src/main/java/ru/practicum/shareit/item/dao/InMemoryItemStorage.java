package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private Long maxId = 0L;

    public Item addItem(Item item) {
        maxId += 1;
        item.setId(maxId);
        items.put(item.getId(), item);

        return item;
    }

    public Item renewItem(Item item) {
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    public Item getItemById(Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new RuntimeException("Данный объект не найден!");
        }

        return items.get(itemId);
    }

    public Collection<Item> getAllItems() {
        return items.values();
    }

    public List<Item> getItemsAfterSearch(String text) {
        List<Item> result = new ArrayList<>();

        for (Item item : items.values()) {
            if ((item.getName().toLowerCase().contains(text.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                    item.getAvailable()) {
                result.add(item);
            }
        }

        return result;
    }
}
