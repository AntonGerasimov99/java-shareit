package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(ItemDto itemDto, Integer userId);

    ItemDto get(Integer itemId);

    ItemDto update(ItemDto itemDto, Integer userId);

    List<ItemDto> getAllItemsByUserId(Integer userId);

    ItemDto getItem(Integer itemId, Integer userId);

    List<ItemDto> search(String text);

    void deleteItem(Integer itemId);

    CommentDto createComment(Integer userId, Integer itemId, CommentDto commentDto);
}
