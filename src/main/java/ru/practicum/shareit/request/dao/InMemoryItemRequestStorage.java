package ru.practicum.shareit.request.dao;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Primary
@AllArgsConstructor
public class InMemoryItemRequestStorage implements ItemRequestStorage {
    private final Map<Long, ItemRequest> requests = new HashMap<>();

    public ItemRequest returnRequestById(Long id) {
        return requests.get(id);
    }

    public List<ItemRequest> getAllRequests() {
        return new ArrayList<>(requests.values());
    }
}
