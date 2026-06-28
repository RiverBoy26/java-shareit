package ru.practicum.gateway.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.user.dto.UserDto;

@Component
public class UserClient extends BaseClient {
    private final String serverUrl;

    public UserClient(@Value("${shareit-server.url}") String serverUrl,
                      RestTemplateBuilder builder) {
        super(builder);
        this.serverUrl = serverUrl + "/users";
    }

    public ResponseEntity<Object> addUser(UserDto userDto) {
        return post(serverUrl, userDto);
    }

    public ResponseEntity<Object> renewUser(Long userId, UserDto userDto) {
        return patch(serverUrl + "/" + userId, userDto);
    }

    public ResponseEntity<Object> getUserById(Long userId) {
        return get(serverUrl + "/" + userId);
    }

    public ResponseEntity<Object> deleteUser(Long userId) {
        return delete(serverUrl + "/" + userId);
    }
}
