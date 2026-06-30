package ru.practicum.gateway.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.gateway.request.dto.ItemRequestDto;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private static final String HEADER = "X-Sharer-User-Id";

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                                 @RequestHeader(HEADER) Long userId) {
        return itemRequestClient.addItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemRequestsByUser(@RequestHeader(HEADER) Long userId) {
        return itemRequestClient.getAllItemRequestsByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllOtherItemRequests(@RequestHeader(HEADER) Long userId) {
        return itemRequestClient.getAllOtherItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader(HEADER) Long userId,
                                                     @PathVariable Long requestId) {
        return itemRequestClient.getItemRequestById(userId, requestId);
    }
}
