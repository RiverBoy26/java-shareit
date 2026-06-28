package ru.practicum.gateway.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.request.dto.ItemRequestDto;

@Component
public class ItemRequestClient extends BaseClient {
    private final String serverUrl;

    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl,
                             RestTemplateBuilder builder) {
        super(builder);
        this.serverUrl = serverUrl + "/requests";
    }

    public ResponseEntity<Object> addItemRequest(ItemRequestDto itemRequestDto,
                                                 Long userId) {
        return post(serverUrl, userId, itemRequestDto);
    }

    public ResponseEntity<Object> getAllItemRequestsByUser(Long userId) {
        return get(serverUrl, userId);
    }

    public ResponseEntity<Object> getAllOtherItemRequests(Long userId) {
        return get(serverUrl + "/all", userId);
    }

    public ResponseEntity<Object> getItemRequestById(Long userId,
                                                     Long requestId) {
        return get(serverUrl + "/" + requestId, userId);
    }
}
