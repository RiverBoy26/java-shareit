package ru.practicum.server.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.service.BookingService;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.exception.NullValueException;
import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.service.UserService;

import java.time.LocalDateTime;
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
class ItemServiceImplIntegrationTest {
    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    @Test
    void addItem_shouldSaveItem() {
        UserDto owner = createUser("Owner", "owner@test.com");
        ItemDto item = createItemDto("Дрель", "Аккумуляторная дрель", true);

        ItemDto saved = itemService.addItem(item, owner.getId());

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Дрель");
        assertThat(saved.getDescription()).isEqualTo("Аккумуляторная дрель");
        assertThat(saved.getAvailable()).isTrue();
    }

    @Test
    void addItem_shouldThrowWhenNameIsBlank() {
        UserDto owner = createUser("Owner", "owner@test.com");
        ItemDto item = createItemDto(" ", "Описание", true);

        assertThatThrownBy(() -> itemService.addItem(item, owner.getId()))
                .isInstanceOf(NullValueException.class);
    }

    @Test
    void renewItem_shouldUpdateItemWhenUserIsOwner() {
        UserDto owner = createUser("Owner", "owner@test.com");
        ItemDto saved = itemService.addItem(createItemDto("Дрель", "Описание", true), owner.getId());

        ItemDto patch = new ItemDto();
        patch.setName("Шуруповёрт");
        patch.setAvailable(false);

        ItemDto updated = itemService.renewItem(patch, saved.getId(), owner.getId());

        assertThat(updated.getName()).isEqualTo("Шуруповёрт");
        assertThat(updated.getDescription()).isEqualTo("Описание");
        assertThat(updated.getAvailable()).isFalse();
    }

    @Test
    void renewItem_shouldThrowWhenUserIsNotOwner() {
        UserDto owner = createUser("Owner", "owner@test.com");
        UserDto other = createUser("Other", "other@test.com");
        ItemDto saved = itemService.addItem(createItemDto("Дрель", "Описание", true), owner.getId());

        ItemDto patch = new ItemDto();
        patch.setName("Новое имя");

        assertThatThrownBy(() -> itemService.renewItem(patch, saved.getId(), other.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getAllItems_shouldReturnOnlyOwnerItems() {
        UserDto owner = createUser("Owner", "owner@test.com");
        UserDto other = createUser("Other", "other@test.com");

        itemService.addItem(createItemDto("Дрель", "Описание", true), owner.getId());
        itemService.addItem(createItemDto("Молоток", "Описание", true), owner.getId());
        itemService.addItem(createItemDto("Пила", "Описание", true), other.getId());

        List<ItemDto> items = itemService.getAllItems(owner.getId());

        assertThat(items).hasSize(2);
        assertThat(items).extracting(ItemDto::getName)
                .containsExactly("Дрель", "Молоток");
    }

    @Test
    void getItemsAfterSearch_shouldFindAvailableItemsByNameOrDescription() {
        UserDto owner = createUser("Owner", "owner@test.com");

        itemService.addItem(createItemDto("Дрель", "Аккумуляторный инструмент", true), owner.getId());
        itemService.addItem(createItemDto("Молоток", "Обычный молоток", true), owner.getId());
        itemService.addItem(createItemDto("Пила", "Аккумуляторная пила", false), owner.getId());

        List<ItemDto> found = itemService.getItemsAfterSearch("аккумулятор");

        assertThat(found).hasSize(1);
        assertThat(found.getFirst().getName()).isEqualTo("Дрель");
    }

    @Test
    void getItemsAfterSearch_shouldReturnEmptyListWhenTextIsBlank() {
        List<ItemDto> found = itemService.getItemsAfterSearch(" ");

        assertThat(found).isEmpty();
    }

    @Test
    void getItemById_shouldReturnCommentsAndBookingsForOwner() {
        UserDto owner = createUser("Owner", "owner@test.com");
        UserDto booker = createUser("Booker", "booker@test.com");
        ItemDto item = itemService.addItem(createItemDto("Дрель", "Описание", true), owner.getId());

        BookingDto booking = createBookingDto(item.getId(),
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2));
        BookingDto savedBooking = bookingService.addBooking(booking, booker.getId());
        bookingService.bookingConfirm(savedBooking.getId(), owner.getId(), true);

        CommentDto comment = new CommentDto();
        comment.setText("Хорошая вещь");

        sleep(2500);
        itemService.addComment(booker.getId(), item.getId(), comment);

        ItemDto found = itemService.getItemById(item.getId(), owner.getId());

        assertThat(found.getComments()).hasSize(1);
        assertThat(found.getComments().getFirst().getText()).isEqualTo("Хорошая вещь");
        assertThat(found.getLastBooking()).isNotNull();
    }

    @Test
    void addComment_shouldThrowWhenUserDidNotBookItem() {
        UserDto owner = createUser("Owner", "owner@test.com");
        UserDto user = createUser("User", "user@test.com");
        ItemDto item = itemService.addItem(createItemDto("Дрель", "Описание", true), owner.getId());

        CommentDto comment = new CommentDto();
        comment.setText("Комментарий");

        assertThatThrownBy(() -> itemService.addComment(user.getId(), item.getId(), comment))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private UserDto createUser(String name, String email) {
        return userService.addUser(new UserDto(null, name, email));
    }

    private ItemDto createItemDto(String name, String description, Boolean available) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(available);
        return itemDto;
    }

    private BookingDto createBookingDto(Long itemId, LocalDateTime start, LocalDateTime end) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemId);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        return bookingDto;
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(exception);
        }
    }
}