package ru.practicum.server.request.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.exception.NullValueException;
import ru.practicum.server.item.entity.Item;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.entity.ItemRequest;
import ru.practicum.server.request.mapper.ItemRequestMapper;
import ru.practicum.server.request.repository.ItemRequestRepository;
import ru.practicum.server.user.entity.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemRequest returnRequestById(Long requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос вещи не найден"));
    }

    @Override
    @Transactional
    public ItemRequestDto addItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        if (itemRequestDto == null
                || itemRequestDto.getDescription() == null
                || itemRequestDto.getDescription().isBlank()) {
            throw new NullValueException("Описание запроса не может быть пустым");
        }

        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        ItemRequest request = ItemRequestMapper.toEntity(itemRequestDto, requestor);
        request.setCreated(LocalDateTime.now());

        return ItemRequestMapper.toDto(itemRequestRepository.save(request), List.of());
    }

    @Override
    @Transactional
    public List<ItemRequestDto> getAllItemRequestsByUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
        Map<Long, List<Item>> answersByRequestId = getAnswersByRequestId(requests);

        return requests.stream()
                .map(request -> ItemRequestMapper.toDto(
                        request,
                        answersByRequestId.getOrDefault(request.getId(), List.of())
                ))
                .toList();
    }

    @Override
    @Transactional
    public List<ItemRequestDto> getAllOtherItemRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId);
        Map<Long, List<Item>> answersByRequestId = getAnswersByRequestId(requests);

        return requests.stream()
                .map(request -> ItemRequestMapper.toDto(
                        request,
                        answersByRequestId.getOrDefault(request.getId(), List.of())
                ))
                .toList();
    }

    @Override
    @Transactional
    public ItemRequestDto getItemRequestById(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос вещи не найден"));

        return ItemRequestMapper.toDto(request, itemRepository.findAllByRequestId(requestId));
    }

    private Map<Long, List<Item>> getAnswersByRequestId(Collection<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();

        return itemRepository.findAllByRequestIdIn(requestIds).stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));
    }
}
