package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage extends JpaRepository<Item, Integer> {

    // ByDescriptionContainsIgnoreCase
    List<Item> findAllByDescription(String text);

    List<Item> findAllByOwnerId(Integer ownerId);
}
