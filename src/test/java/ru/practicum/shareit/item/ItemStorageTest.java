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
    private Item item2;
    private User user;
    private User user2;
    private ItemRequest itemRequest;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .name("UserName")
                .email("new@mail.ru")
                .build();

        user2 = User.builder()
                .name("UserName2")
                .email("new2@mail.ru")
                .build();

        item = Item.builder()
                .name("name")
                .description("description")
                .available(true)
                .owner(user2)
                .build();

        item2 = Item.builder()
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

        user = userStorage.save(user);
        user2 = userStorage.save(user2);
        item = itemStorage.save(item);
        item2 = itemStorage.save(item2);
    }

    @Test
    void shouldFindAllByDescriptionContainsIgnoreCase() {
        List<Item> result = itemStorage.findAllByDescriptionContainsIgnoreCase("one", Pageable.unpaged());

        Assertions.assertNotNull(result);
        assertThat(item2.getId(), equalTo(result.get(0).getId()));
        assertThat(item2.getDescription(), equalTo(result.get(0).getDescription()));
        assertThat(item2.getAvailable(), equalTo(result.get(0).getAvailable()));
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
