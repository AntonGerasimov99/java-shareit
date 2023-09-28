package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundElementException;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.request.utils.ItemRequestUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestStorage itemRequestStorage;
    private final ItemStorage itemStorage;
    private final ItemRequestUtils utils;

    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Integer userId) {
        ItemRequest itemRequest = utils.validationRequest(itemRequestDto, userId);
        return ItemRequestMapper.toItemRequestDto(itemRequestStorage.save(itemRequest), null);
    }

    @Override
    public ItemRequestDto findByUserIdAndRequestId(Integer userId, Integer requestId) {
        utils.isUser(userId);
        ItemRequest itemRequest = itemRequestStorage.findById(requestId)
                .orElseThrow(() -> new NotFoundElementException("Запрос не найден"));
        return utils.addItemsAndConvertToDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> findAllByUserId(Integer userId) {
        utils.isUser(userId);
        return itemRequestStorage.findAllByRequesterId(userId).stream()
                .map(utils::addItemsAndConvertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> findAllPageableByUserId(Integer userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return itemRequestStorage.findAllByRequesterIdIsNotOrderByCreatedDesc(userId, pageable).stream()
                .map(utils::addItemsAndConvertToDto)
                .collect(Collectors.toList());
    }
}
