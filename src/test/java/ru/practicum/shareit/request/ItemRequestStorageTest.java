package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
public class ItemRequestStorageTest {

    @Autowired
    private ItemStorage itemStorage;
    @Autowired
    private UserStorage userStorage;
    @Autowired
    private ItemRequestStorage itemRequestStorage;
    private Item item;
    private User user;
    private User user2;
    private ItemRequest itemRequest;
    private LocalDateTime createdDate;


    @BeforeEach
    void beforeEach() {
        createdDate = LocalDateTime.now();

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

        user = userStorage.save(user);
        user2 = userStorage.save(user2);
        item = itemStorage.save(item);

        itemRequest = ItemRequest.builder()
                .description("description")
                .requester(user)
                .created(createdDate)
                .build();

        itemRequest = itemRequestStorage.save(itemRequest);
    }

    @Test
    void shouldFindAllByRequesterIdIsNotOrder() {
        List<ItemRequest> result = itemRequestStorage.findAllByRequesterIdIsNotOrderByCreatedDesc(2, Pageable.unpaged());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        assertThat(itemRequest.getId(), equalTo(result.get(0).getId()));
        assertThat(itemRequest.getDescription(), equalTo(result.get(0).getDescription()));
        assertThat(itemRequest.getRequester().getId(), equalTo(result.get(0).getRequester().getId()));
        assertThat(itemRequest.getCreated(), equalTo(result.get(0).getCreated()));
    }

    @Test
    void shouldFindAllByRequesterId() {
        List<ItemRequest> result = itemRequestStorage.findAllByRequesterId(itemRequest.getRequester().getId());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        assertThat(itemRequest.getId(), equalTo(result.get(0).getId()));
        assertThat(itemRequest.getDescription(), equalTo(result.get(0).getDescription()));
        assertThat(itemRequest.getRequester().getId(), equalTo(result.get(0).getRequester().getId()));
        assertThat(itemRequest.getCreated(), equalTo(result.get(0).getCreated()));
    }
}