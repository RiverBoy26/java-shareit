package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.entity.ItemRequest;

public interface ItemRequestService {
    ItemRequest returnRequestById(Long requestId);
}
