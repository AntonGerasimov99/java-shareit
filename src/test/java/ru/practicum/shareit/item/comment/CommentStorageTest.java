package ru.practicum.shareit.item.comment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
public class CommentStorageTest {

    @Autowired
    private BookingStorage bookingStorage;
    @Autowired
    private ItemStorage itemStorage;
    @Autowired
    private UserStorage userStorage;
    @Autowired
    private CommentStorage commentStorage;
    private User user;
    private User user2;
    private Item item;
    private Comment comment;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .id(1)
                .name("name")
                .email("new@mail.ru")
                .build();

        item = Item.builder()
                .id(1)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(user)
                .build();

        user2 = User.builder()
                .id(1)
                .name("name2")
                .email("new2@mail.ru")
                .build();

        comment = Comment.builder()
                .id(1)
                .text("text")
                .item(item)
                .author(user2)
                .date(LocalDateTime.now())
                .build();

        userStorage.save(user);
        userStorage.save(user2);
        itemStorage.save(item);
        commentStorage.save(comment);
    }

    @Test
    void shouldFindAllByItemIdOrderByDate() {
        List<Comment> result = commentStorage.findAllByItemIdOrderByDate(item.getId());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        assertThat(comment.getId(), equalTo(result.get(0).getId()));
        assertThat(comment.getText(), equalTo(result.get(0).getText()));
        assertThat(comment.getItem().getId(), equalTo(result.get(0).getItem().getId()));
        assertThat(comment.getAuthor().getId(), equalTo(result.get(0).getAuthor().getId()));
    }
}
