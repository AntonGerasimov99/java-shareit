package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage extends JpaRepository<Item, Integer> {

    List<Item> findAllByDescriptionContainsIgnoreCase(String text, Pageable pageable);

    List<Item> findAllByOwnerId(Integer ownerId, Pageable pageable);

    List<Item> findAllByRequestId(Integer requestId);
}