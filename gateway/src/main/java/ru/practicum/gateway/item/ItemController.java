package ru.practicum.gateway.item;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.gateway.item.dto.CommentDto;
import ru.practicum.gateway.item.dto.ItemDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final String HEADER = "X-Sharer-User-Id";

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestBody ItemDto itemDto,
                                          @RequestHeader(HEADER) Long userId) {
        validateNewItem(itemDto);
        return itemClient.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> renewItem(@RequestBody ItemDto itemDto,
                                            @PathVariable Long itemId,
                                            @RequestHeader(HEADER) Long userId) {
        validateItemPatch(itemDto);
        return itemClient.renewItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId,
                                              @RequestHeader(HEADER) Long userId) {
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader(HEADER) Long userId) {
        return itemClient.getAllItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text) {
        return itemClient.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(HEADER) Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody CommentDto commentDto) {
        if (commentDto == null || commentDto.getText() == null || commentDto.getText().isBlank()) {
            throw new ValidationException("Текст комментария не может быть пустым");
        }

        return itemClient.addComment(userId, itemId, commentDto);
    }

    private void validateNewItem(ItemDto itemDto) {
        if (itemDto == null) {
            throw new ValidationException("Тело запроса не может быть пустым");
        }

        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }

        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException("Описание не может быть пустым");
        }

        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Статус доступности не может быть пустым");
        }
    }

    private void validateItemPatch(ItemDto itemDto) {
        if (itemDto == null) {
            throw new ValidationException("Тело запроса не может быть пустым");
        }

        if (itemDto.getName() != null && itemDto.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }

        if (itemDto.getDescription() != null && itemDto.getDescription().isBlank()) {
            throw new ValidationException("Описание не может быть пустым");
        }
    }
}
