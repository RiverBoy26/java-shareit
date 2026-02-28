package ru.practicum.shareit.item.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addItem(Item item);

    Item renewItem(Long itemId);

    Item getItemById(Long itemId);

    List<Item> getAllItems();

    List<Item> getItemsAfterSearch(String text);
}
