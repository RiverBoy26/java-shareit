package ru.practicum.gateway.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.gateway.item.dto.CommentDto;
import ru.practicum.gateway.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {
    private static final String HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemClient itemClient;

    @Test
    void addItem_shouldCallClient() throws Exception {
        ItemDto item = new ItemDto();
        item.setName("Дрель");
        item.setDescription("Аккумуляторная дрель");
        item.setAvailable(true);

        when(itemClient.addItem(any(ItemDto.class), eq(1L)))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 1,
                        "name", "Дрель",
                        "description", "Аккумуляторная дрель",
                        "available", true
                )));

        mockMvc.perform(post("/items")
                        .header(HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель"));

        verify(itemClient).addItem(any(ItemDto.class), eq(1L));
    }

    @Test
    void addItem_shouldReturnBadRequestWhenNameBlank() throws Exception {
        ItemDto item = new ItemDto();
        item.setName(" ");
        item.setDescription("Описание");
        item.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header(HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(item)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addItem_shouldReturnBadRequestWhenDescriptionBlank() throws Exception {
        ItemDto item = new ItemDto();
        item.setName("Дрель");
        item.setDescription(" ");
        item.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header(HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(item)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addItem_shouldReturnBadRequestWhenAvailableNull() throws Exception {
        ItemDto item = new ItemDto();
        item.setName("Дрель");
        item.setDescription("Описание");

        mockMvc.perform(post("/items")
                        .header(HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(item)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void renewItem_shouldCallClient() throws Exception {
        ItemDto patch = new ItemDto();
        patch.setName("Новое имя");

        when(itemClient.renewItem(any(ItemDto.class), eq(1L), eq(2L)))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 1,
                        "name", "Новое имя"
                )));

        mockMvc.perform(patch("/items/1")
                        .header(HEADER, 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(patch)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Новое имя"));

        verify(itemClient).renewItem(any(ItemDto.class), eq(1L), eq(2L));
    }

    @Test
    void renewItem_shouldReturnBadRequestWhenNameBlank() throws Exception {
        ItemDto patch = new ItemDto();
        patch.setName(" ");

        mockMvc.perform(patch("/items/1")
                        .header(HEADER, 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(patch)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemById_shouldCallClient() throws Exception {
        when(itemClient.getItemById(1L, 2L))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 1,
                        "name", "Дрель"
                )));

        mockMvc.perform(get("/items/1")
                        .header(HEADER, 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(itemClient).getItemById(1L, 2L);
    }

    @Test
    void getAllItems_shouldCallClient() throws Exception {
        when(itemClient.getAllItems(1L))
                .thenReturn(ResponseEntity.ok(List.of(
                        Map.of("id", 1, "name", "Дрель"),
                        Map.of("id", 2, "name", "Молоток")
                )));

        mockMvc.perform(get("/items")
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(itemClient).getAllItems(1L);
    }

    @Test
    void search_shouldCallClient() throws Exception {
        when(itemClient.search("дрель"))
                .thenReturn(ResponseEntity.ok(List.of(
                        Map.of("id", 1, "name", "Дрель")
                )));

        mockMvc.perform(get("/items/search")
                        .param("text", "дрель"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(itemClient).search("дрель");
    }

    @Test
    void addComment_shouldCallClient() throws Exception {
        CommentDto comment = new CommentDto();
        comment.setText("Хорошая вещь");

        when(itemClient.addComment(eq(1L), eq(2L), any(CommentDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 1,
                        "text", "Хорошая вещь",
                        "authorName", "User",
                        "created", LocalDateTime.now().toString()
                )));

        mockMvc.perform(post("/items/2/comment")
                        .header(HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Хорошая вещь"));

        verify(itemClient).addComment(eq(1L), eq(2L), any(CommentDto.class));
    }

    @Test
    void addComment_shouldReturnBadRequestWhenTextBlank() throws Exception {
        CommentDto comment = new CommentDto();
        comment.setText(" ");

        mockMvc.perform(post("/items/2/comment")
                        .header(HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(comment)))
                .andExpect(status().isBadRequest());
    }
}