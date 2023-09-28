package ru.practicum.shareit.request.utils;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundElementException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ItemRequestUtils {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemRequest validationRequest(ItemRequestDto itemRequestDto, Integer userId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundElementException("Пользователь не найден"));
        return ItemRequestMapper.toItemRequestFromDto(itemRequestDto, user);
    }

    public void isUser(Integer userId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundElementException("Пользователь не найден"));
    }

    /*public List<ItemDto> findItemsForRequest(Integer requestId) {
        return itemStorage.findAllByRequestId(requestId).stream()
                .map(ItemMapper::toItemDTO)
                .collect(Collectors.toList());
    }*/

    public ItemRequestDto addItemsAndConvertToDto(ItemRequest itemRequest) {
        List<Item> items = itemStorage.findAllByRequestId(itemRequest.getId());
        List<ItemDto> result = items.stream()
                .map(ItemMapper::toItemDTO)
                .collect(Collectors.toList());
        //Добавить проверку на нул?
        return ItemRequestMapper.toItemRequestDto(itemRequest, result);
    }
}
