package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item create(Item item);

    Item get(Integer itemId);

    Item update(Item item);

    List<Item> getItemsByUserId(Integer userId);

    List<Item> search(String text);

    void deleteItem(Integer itemId);

    boolean isItem(Integer itemId);
}
