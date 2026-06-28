package ru.practicum.gateway.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.gateway.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {
    private static final String HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Test
    void addItemRequest_shouldCallClient() throws Exception {
        ItemRequestDto request = new ItemRequestDto();
        request.setDescription("Нужна дрель");

        when(itemRequestClient.addItemRequest(any(ItemRequestDto.class), eq(1L)))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 1,
                        "description", "Нужна дрель",
                        "created", LocalDateTime.now().toString()
                )));

        mockMvc.perform(post("/requests")
                        .header(HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Нужна дрель"));

        verify(itemRequestClient).addItemRequest(any(ItemRequestDto.class), eq(1L));
    }

    @Test
    void addItemRequest_shouldReturnBadRequestWhenDescriptionBlank() throws Exception {
        ItemRequestDto request = new ItemRequestDto();
        request.setDescription(" ");

        mockMvc.perform(post("/requests")
                        .header(HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllItemRequestsByUser_shouldCallClient() throws Exception {
        when(itemRequestClient.getAllItemRequestsByUser(1L))
                .thenReturn(ResponseEntity.ok(List.of(
                        Map.of("id", 1, "description", "Первый"),
                        Map.of("id", 2, "description", "Второй")
                )));

        mockMvc.perform(get("/requests")
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(itemRequestClient).getAllItemRequestsByUser(1L);
    }

    @Test
    void getAllOtherItemRequests_shouldCallClient() throws Exception {
        when(itemRequestClient.getAllOtherItemRequests(1L))
                .thenReturn(ResponseEntity.ok(List.of(
                        Map.of("id", 1, "description", "Чужой запрос")
                )));

        mockMvc.perform(get("/requests/all")
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(itemRequestClient).getAllOtherItemRequests(1L);
    }

    @Test
    void getItemRequestById_shouldCallClient() throws Exception {
        when(itemRequestClient.getItemRequestById(1L, 2L))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 2,
                        "description", "Нужна дрель",
                        "items", List.of(Map.of(
                                "id", 10,
                                "name", "Дрель",
                                "ownerId", 3
                        ))
                )));

        mockMvc.perform(get("/requests/2")
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.items[0].name").value("Дрель"));

        verify(itemRequestClient).getItemRequestById(1L, 2L);
    }
}