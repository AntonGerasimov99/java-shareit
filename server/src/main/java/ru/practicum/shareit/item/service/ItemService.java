package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(ItemDto itemDto, Integer userId);

    ItemDto get(Integer itemId);

    ItemDto update(ItemDto itemDto, Integer userId);

    List<ItemDto> getAllItemsByUserId(Integer userId, Integer from, Integer size);

    ItemDto getItem(Integer itemId, Integer userId);

    List<ItemDto> search(String text, Integer from, Integer size);

    CommentDto createComment(Integer userId, Integer itemId, CommentDto commentDto);
}