package ru.practicum.server.request.service;

import org.springframework.web.bind.annotation.RequestHeader;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.entity.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequest returnRequestById(Long requestId);

    ItemRequestDto addItemRequest(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDto> getAllItemRequestsByUser(Long userId);

    List<ItemRequestDto> getAllOtherItemRequests(Long userId);

    ItemRequestDto getItemRequestById(Long userId, Long requestId);
}
