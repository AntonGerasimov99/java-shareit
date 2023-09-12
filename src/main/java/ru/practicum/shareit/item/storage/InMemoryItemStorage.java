package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundElementException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryItemStorage implements ItemStorage {

    private static int keyId = 0;
    private final Map<Integer, Item> items = new HashMap<>();

    @Override
    public Item create(Item item) {
        Integer newId = generateId();
        item.setId(newId);
        items.put(item.getId(), item);
        return get(item.getId());
    }

    @Override
    public Item get(Integer itemId) {
        if (!isItem(itemId)) {
            throw new NotFoundElementException();
        }
        return items.get(itemId);
    }

    @Override
    public Item update(Item item) {
        item = validateItemForUpdate(item);
        items.put(item.getId(), item);
        return get(item.getId());
    }

    @Override
    public List<Item> getItemsByUserId(Integer userId) {
        List<Item> result = items.values().stream()
                .filter(item -> Objects.equals(item.getOwner(), userId))
                .collect(Collectors.toList());
        return result;
    }

    public void deleteItem(Integer itemId) {
        items.remove(itemId);
    }

    @Override
    public List<Item> search(String text) {
        List<Item> result = items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getDescription().toUpperCase()
                        .contains(text.toUpperCase()))
                .collect(Collectors.toList());
        return result;
    }

    public static int generateId() {
        return ++keyId;
    }

    @Override
    public boolean isItem(Integer itemId) {
        return items.containsKey(itemId);
    }

    public Item validateItemForUpdate(Item item) {
        Item oldItem = items.get(item.getId());
        if (item.getName() == null || item.getName().isBlank()) {
            item.setName(oldItem.getName());
        }
        if (item.getDescription() == null || item.getDescription().isEmpty()) {
            item.setDescription(oldItem.getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(oldItem.getAvailable());
        }
        return item;
    }
}
