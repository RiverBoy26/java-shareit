package ru.practicum.server.request.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.exception.NullValueException;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.service.ItemService;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.entity.ItemRequest;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Sql(
        statements = {
                "DELETE FROM comments",
                "DELETE FROM bookings",
                "DELETE FROM items",
                "DELETE FROM requests",
                "DELETE FROM users"
        },
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class ItemRequestServiceImplIntegrationTest {
    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Test
    void addItemRequest_shouldCreateRequest() {
        UserDto user = createUser("User", "user@test.com");

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Нужна дрель");

        ItemRequestDto saved = itemRequestService.addItemRequest(requestDto, user.getId());

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getDescription()).isEqualTo("Нужна дрель");
        assertThat(saved.getCreated()).isNotNull();
        assertThat(saved.getItems()).isEmpty();
    }

    @Test
    void addItemRequest_shouldThrowWhenDescriptionIsBlank() {
        UserDto user = createUser("User", "user@test.com");

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription(" ");

        assertThatThrownBy(() -> itemRequestService.addItemRequest(requestDto, user.getId()))
                .isInstanceOf(NullValueException.class);
    }

    @Test
    void returnRequestById_shouldReturnRequest() {
        UserDto user = createUser("User", "user@test.com");
        ItemRequestDto saved = itemRequestService.addItemRequest(createRequestDto("Нужна дрель"), user.getId());

        ItemRequest request = itemRequestService.returnRequestById(saved.getId());

        assertThat(request.getId()).isEqualTo(saved.getId());
        assertThat(request.getDescription()).isEqualTo("Нужна дрель");
    }

    @Test
    void returnRequestById_shouldThrowWhenRequestDoesNotExist() {
        assertThatThrownBy(() -> itemRequestService.returnRequestById(999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getAllItemRequestsByUser_shouldReturnUserRequestsFromNewestToOldest() {
        UserDto user = createUser("User", "user@test.com");

        ItemRequestDto first = itemRequestService.addItemRequest(createRequestDto("Первый запрос"), user.getId());
        ItemRequestDto second = itemRequestService.addItemRequest(createRequestDto("Второй запрос"), user.getId());

        List<ItemRequestDto> requests = itemRequestService.getAllItemRequestsByUser(user.getId());

        assertThat(requests).hasSize(2);
        assertThat(requests).extracting(ItemRequestDto::getId)
                .containsExactly(second.getId(), first.getId());
    }

    @Test
    void getAllOtherItemRequests_shouldReturnOnlyOtherUsersRequests() {
        UserDto requestor = createUser("Requestor", "requestor@test.com");
        UserDto viewer = createUser("Viewer", "viewer@test.com");

        itemRequestService.addItemRequest(createRequestDto("Чужой запрос"), requestor.getId());
        itemRequestService.addItemRequest(createRequestDto("Мой запрос"), viewer.getId());

        List<ItemRequestDto> requests = itemRequestService.getAllOtherItemRequests(viewer.getId());

        assertThat(requests).hasSize(1);
        assertThat(requests.getFirst().getDescription()).isEqualTo("Чужой запрос");
    }

    @Test
    void getItemRequestById_shouldReturnRequestWithAnswers() {
        UserDto requestor = createUser("Requestor", "requestor@test.com");
        UserDto owner = createUser("Owner", "owner@test.com");

        ItemRequestDto request = itemRequestService.addItemRequest(createRequestDto("Нужна дрель"), requestor.getId());

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Дрель");
        itemDto.setDescription("Аккумуляторная дрель");
        itemDto.setAvailable(true);
        itemDto.setRequestId(request.getId());

        ItemDto item = itemService.addItem(itemDto, owner.getId());

        ItemRequestDto found = itemRequestService.getItemRequestById(owner.getId(), request.getId());

        assertThat(found.getId()).isEqualTo(request.getId());
        assertThat(found.getItems()).hasSize(1);
        assertThat(found.getItems().getFirst().getId()).isEqualTo(item.getId());
        assertThat(found.getItems().getFirst().getName()).isEqualTo("Дрель");
        assertThat(found.getItems().getFirst().getOwnerId()).isEqualTo(owner.getId());
    }

    private UserDto createUser(String name, String email) {
        return userService.addUser(new UserDto(null, name, email));
    }

    private ItemRequestDto createRequestDto(String description) {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription(description);
        return requestDto;
    }
}