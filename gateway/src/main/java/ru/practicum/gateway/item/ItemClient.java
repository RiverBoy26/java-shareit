package ru.practicum.gateway.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.item.dto.CommentDto;
import ru.practicum.gateway.item.dto.ItemDto;

@Component
public class ItemClient extends BaseClient {
    private final String serverUrl;

    public ItemClient(@Value("${shareit-server.url}") String serverUrl,
                      RestTemplateBuilder builder) {
        super(builder);
        this.serverUrl = serverUrl + "/items";
    }

    public ResponseEntity<Object> addItem(ItemDto itemDto, Long userId) {
        return post(serverUrl, userId, itemDto);
    }

    public ResponseEntity<Object> renewItem(ItemDto itemDto, Long itemId, Long userId) {
        return patch(serverUrl + "/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> getItemById(Long itemId, Long userId) {
        return get(serverUrl + "/" + itemId, userId);
    }

    public ResponseEntity<Object> getAllItems(Long userId) {
        return get(serverUrl, userId);
    }

    public ResponseEntity<Object> search(String text) {
        return get(serverUrl + "/search?text=" + text);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentDto commentDto) {
        return post(serverUrl + "/" + itemId + "/comment", userId, commentDto);
    }
}
