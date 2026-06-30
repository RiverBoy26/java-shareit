package ru.practicum.server.request.dao;

import ru.practicum.server.request.entity.ItemRequest;

import java.util.List;

public interface ItemRequestStorage {
    ItemRequest returnRequestById(Long id);

    List<ItemRequest> getAllRequests();
}
