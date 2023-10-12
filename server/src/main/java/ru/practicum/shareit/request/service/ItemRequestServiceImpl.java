package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundElementException;
import ru.practicum.shareit.exceptions.ValidationElementException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestStorage itemRequestStorage;
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Integer userId) {
        ItemRequest itemRequest = validationRequest(itemRequestDto, userId);
        return ItemRequestMapper.toItemRequestDto(itemRequestStorage.save(itemRequest), null);
    }

    @Override
    public ItemRequestDto findByUserIdAndRequestId(Integer userId, Integer requestId) {
        isUser(userId);
        ItemRequest itemRequest = itemRequestStorage.findById(requestId)
                .orElseThrow(() -> new NotFoundElementException("Запрос не найден"));
        return addItemsAndConvertToDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> findAllByUserId(Integer userId) {
        isUser(userId);
        return itemRequestStorage.findAllByRequesterId(userId).stream()
                .map(this::addItemsAndConvertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> findAllPageableByUserId(Integer userId, Integer from, Integer size) {
        if (from < 0) {
            throw new ValidationElementException("From меньше 0");
        }
        Pageable pageable = PageRequest.of(from / size, size);
        return itemRequestStorage.findAllByRequesterIdIsNotOrderByCreatedDesc(userId, pageable).stream()
                .map(this::addItemsAndConvertToDto)
                .collect(Collectors.toList());
    }

    public ItemRequest validationRequest(ItemRequestDto itemRequestDto, Integer userId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundElementException("Пользователь не найден"));
        return ItemRequestMapper.toItemRequestFromDto(itemRequestDto, user);
    }

    public void isUser(Integer userId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundElementException("Пользователь не найден"));
    }

    public ItemRequestDto addItemsAndConvertToDto(ItemRequest itemRequest) {
        List<Item> items = itemStorage.findAllByRequestId(itemRequest.getId());
        List<ItemDto> result = items.stream()
                .map(ItemMapper::toItemDTO)
                .collect(Collectors.toList());
        return ItemRequestMapper.toItemRequestDto(itemRequest, result);
    }
}
