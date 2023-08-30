package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundElementException;
import ru.practicum.shareit.exceptions.ValidationElementException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto create(ItemDto itemDto, Integer userId) {
        isUser(userId);
        checkItemValid(itemDto);
        itemDto.setOwner(userId);
        Item item = itemMapper.toItemFromDTO(itemDto);
        itemStorage.create(item);
        return get(item.getId());
    }

    @Override
    public ItemDto get(Integer itemId) {
        return itemMapper.toItemDTO(itemStorage.get(itemId));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Integer userId) {
        isItem(itemDto.getId());
        isUser(userId);
        isUserOwner(userId, itemDto.getId());
        itemDto.setOwner(userId);
        Item item = itemMapper.toItemFromDTO(itemDto);
        return itemMapper.toItemDTO(itemStorage.update(item));
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(Integer userId) {
        isUser(userId);
        List<Item> items = itemStorage.getItemsByUserId(userId);
        return items.stream()
                .map(itemMapper::toItemDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        List<Item> items = itemStorage.search(text);
        return items.stream()
                .map(itemMapper::toItemDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(Integer itemId) {
        isItem(itemId);
        itemStorage.deleteItem(itemId);
    }

    private void isItem(Integer itemId) {
        if (itemId == null || !itemStorage.isItem(itemId)) {
            throw new NotFoundElementException();
        }
    }

    private void checkItemValid(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationElementException("Имя отсутствует");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationElementException("Описание отсутствует");
        }
    }

    private void isUser(Integer userId) {
        if (userId == null || !userStorage.isUser(userId)) {
            throw new NotFoundElementException();
        }
    }

    private void isUserOwner(Integer userId, Integer itemId) {
        if (!Objects.equals(itemStorage.get(itemId).getOwner(), userId)) {
            throw new NotFoundElementException();
        }
    }
}
