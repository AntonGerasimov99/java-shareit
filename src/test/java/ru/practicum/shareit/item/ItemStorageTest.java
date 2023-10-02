package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
public class ItemStorageTest {

    @Autowired
    private ItemStorage itemStorage;
    @Autowired
    private UserStorage userStorage;
    private Item item;
    private User user;
    private User user2;
    private ItemRequest itemRequest;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .id(1)
                .name("UserName")
                .email("new@mail.ru")
                .build();

        user2 = User.builder()
                .id(2)
                .name("UserName2")
                .email("new2@mail.ru")
                .build();

        item = Item.builder()
                .id(1)
                .name("name")
                .description("description one")
                .available(true)
                .owner(user2)
                .build();

        itemRequest = ItemRequest.builder()
                .description("description")
                .requester(user)
                .created(LocalDateTime.now())
                .build();

        userStorage.save(user);
        userStorage.save(user2);
        itemStorage.save(item);
    }

    @Test
    void shouldFindAllByDescriptionContainsIgnoreCase() {
        List<Item> result = itemStorage.findAllByDescriptionContainsIgnoreCase("one", Pageable.unpaged());

        Assertions.assertNotNull(result);
        assertThat(item.getId(), equalTo(result.get(0).getId()));
        assertThat(item.getDescription(), equalTo(result.get(0).getDescription()));
        assertThat(item.getAvailable(), equalTo(result.get(0).getAvailable()));
    }

    @Test
    void shouldFindAllByOwner() {
        List<Item> result = itemStorage.findAllByOwnerId(user2.getId(), Pageable.unpaged());

        Assertions.assertNotNull(result);
        assertThat(item.getId(), equalTo(result.get(0).getId()));
        assertThat(item.getDescription(), equalTo(result.get(0).getDescription()));
        assertThat(item.getAvailable(), equalTo(result.get(0).getAvailable()));
    }
}
