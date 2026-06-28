package ru.practicum.gateway.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.gateway.user.dto.UserDto;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserClient userClient;

    @Test
    void addUser_shouldReturnCreatedUser() throws Exception {
        UserDto user = new UserDto(null, "User", "user@test.com");

        when(userClient.addUser(any(UserDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 1,
                        "name", "User",
                        "email", "user@test.com"
                )));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("User"))
                .andExpect(jsonPath("$.email").value("user@test.com"));

        verify(userClient).addUser(any(UserDto.class));
    }

    @Test
    void addUser_shouldReturnBadRequestWhenEmailInvalid() throws Exception {
        UserDto user = new UserDto(null, "User", "bad-email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void renewUser_shouldCallClient() throws Exception {
        UserDto patch = new UserDto(null, "New name", "new@test.com");

        when(userClient.renewUser(eq(1L), any(UserDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 1,
                        "name", "New name",
                        "email", "new@test.com"
                )));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(patch)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New name"));

        verify(userClient).renewUser(eq(1L), any(UserDto.class));
    }

    @Test
    void renewUser_shouldReturnBadRequestWhenEmailInvalid() throws Exception {
        UserDto patch = new UserDto(null, null, "bad-email");

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(patch)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserById_shouldCallClient() throws Exception {
        when(userClient.getUserById(1L))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 1,
                        "name", "User",
                        "email", "user@test.com"
                )));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(userClient).getUserById(1L);
    }

    @Test
    void deleteUser_shouldCallClient() throws Exception {
        when(userClient.deleteUser(1L))
                .thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        verify(userClient).deleteUser(1L);
    }
}