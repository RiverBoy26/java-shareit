package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dao.ItemRequestStorage;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl {
    private final ItemRequestStorage itemRequestStorage;

    public ItemRequest returnRequestById(Long requestId) {
        List<Long> requests = itemRequestStorage.getAllRequests().stream().map(ItemRequest::getId).toList();

        if (!requests.contains(requestId)) {
            throw new RuntimeException("Данный запрос не найден");
        }

        return returnRequestById(requestId);
    }
}
