package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    public Item addItem(Item item) {
        return new Item();
    }

    public Item renewItem(Long itemId) {
        return new Item();
    }

    public Item getItemById(Long itemId) {
        return new Item();
    }

    public List<Item> getAllItems() {
        return new ArrayList<>();
    }

    public List<Item> getItemsAfterSearch(String text) {
        return new ArrayList<>();
    }
}
