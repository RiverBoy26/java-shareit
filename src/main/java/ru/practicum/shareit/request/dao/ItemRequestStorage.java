package ru.practicum.shareit.request.dao;

import ru.practicum.shareit.request.entity.ItemRequest;

import java.util.List;

public interface ItemRequestStorage {
    ItemRequest returnRequestById(Long id);

    List<ItemRequest> getAllRequests();
}
