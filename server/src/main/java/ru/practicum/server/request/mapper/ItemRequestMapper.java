package ru.practicum.server.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.server.item.entity.Item;
import ru.practicum.server.request.dto.ItemRequestAnswerDto;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.entity.ItemRequest;
import ru.practicum.server.user.entity.User;

import java.util.List;

@UtilityClass
public class ItemRequestMapper {
    public ItemRequest toEntity(ItemRequestDto dto, User requestor) {
        ItemRequest request = new ItemRequest();
        request.setDescription(dto.getDescription());
        request.setRequestor(requestor);
        return request;
    }

    public ItemRequestDto toDto(ItemRequest request, List<Item> answers) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        dto.setItems(answers.stream()
                .map(ItemRequestMapper::toAnswerDto)
                .toList());
        return dto;
    }

    public ItemRequestAnswerDto toAnswerDto(Item item) {
        return new ItemRequestAnswerDto(
                item.getId(),
                item.getName(),
                item.getOwner().getId()
        );
    }
}
