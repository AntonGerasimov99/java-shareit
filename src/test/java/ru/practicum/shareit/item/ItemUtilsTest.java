package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NotFoundElementException;
import ru.practicum.shareit.exceptions.ValidationElementException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.utils.ItemUtils;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class ItemUtilsTest {

    @Mock
    private ItemStorage itemStorage;
    @Mock
    private UserStorage userStorage;
    @Mock
    private BookingService bookingService;
    @InjectMocks
    private ItemUtils utils;
    private ItemDto itemDto;
    private Item item;
    private UserDto userDto;
    private User user;

    @BeforeEach
    void beforeEach() {
        userDto = UserDto.builder()
                .id(1)
                .name("name")
                .email("new@mail.ru")
                .build();

        user = User.builder()
                .id(1)
                .name("name")
                .email("new@mail.ru")
                .build();
        itemDto = ItemDto.builder()
                .id(1)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(1)
                .build();

        item = Item.builder()
                .id(1)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(user)
                .build();
    }

    @Test
    void shouldValidateItemForUpdate() {
        Item itemForUpdate = Item.builder().id(1).build();
        Mockito.when(itemStorage.findById(itemForUpdate.getId())).thenReturn(Optional.ofNullable(item));

        Item result = utils.validateItemForUpdate(itemForUpdate);

        assertThat(item.getId(), equalTo(result.getId()));
        assertThat(item.getName(), equalTo(result.getName()));
        assertThat(item.getDescription(), equalTo(result.getDescription()));
        assertThat(item.getAvailable(), equalTo(result.getAvailable()));

        Mockito.verify(itemStorage, times(1)).findById(itemForUpdate.getId());
    }

    @Test
    void shouldNotValidate() {
        Assertions.assertThrows(NotFoundElementException.class, () -> utils.validateItemForUpdate(item));
    }

    @Test
    void shouldCheckItemWithoutName() {
        itemDto.setName(null);
        Assertions.assertThrows(ValidationElementException.class, () -> utils.checkItemValid(itemDto));
    }

    @Test
    void shouldCheckItemWithoutDescription() {
        itemDto.setDescription(null);
        Assertions.assertThrows(ValidationElementException.class, () -> utils.checkItemValid(itemDto));
    }

    @Test
    void shouldCheckIsItem() {
        Assertions.assertThrows(NotFoundElementException.class, () -> utils.isItem(55));
    }

    @Test
    void shouldCheckIsUser() {
        Assertions.assertThrows(NotFoundElementException.class, () -> utils.isUser(55));
    }

    @Test
    void shouldCheckIsUserOwner() {
        Mockito.when(itemStorage.getById(1)).thenReturn(item);
        Assertions.assertThrows(NotFoundElementException.class, () -> utils.isUserOwner(55, 1));
    }

    @Test
    void shouldCheckIsBooking() {
        List<BookingDto> result = new ArrayList<>();
        Mockito.when(bookingService.findAllByBooker(user.getId(), "ALL", 0, 10)).thenReturn(result);
        Assertions.assertThrows(ValidationElementException.class, () -> utils.isBooking(user.getId(), 15));
    }

    @Test
    void shouldCreateComment() {
        Mockito.when(itemStorage.findById(1)).thenReturn(Optional.ofNullable(item));
        Mockito.when(userStorage.findById(1)).thenReturn(Optional.ofNullable(user));
        CommentDto commentDto = CommentDto.builder()
                .id(1)
                .text("sa")
                .item("1")
                .authorName("1")
                .build();

        Comment comment = Comment.builder()
                .id(1)
                .text("sa")
                .item(item)
                .author(user)
                .build();

        Comment result = utils.createComment(1, 1, commentDto);

        assertThat(comment.getId(), equalTo(result.getId()));
        assertThat(comment.getText(), equalTo(result.getText()));
        assertThat(comment.getItem().getId(), equalTo(result.getItem().getId()));
        assertThat(comment.getAuthor().getId(), equalTo(result.getAuthor().getId()));
    }

    @Test
    void shouldNotCreateCommentWithEmptyText() {
        CommentDto commentDto = CommentDto.builder()
                .id(1)
                .text("")
                .item("1")
                .authorName("1")
                .build();
        Assertions.assertThrows(ValidationElementException.class, () -> utils.createComment(user.getId(), item.getId(), commentDto));
    }

    @Test
    void shouldNotCreateCommentWithoutItem() {
        CommentDto commentDto = CommentDto.builder()
                .id(1)
                .text("sa")
                .item("1")
                .authorName("1")
                .build();
        Assertions.assertThrows(NotFoundElementException.class, () -> utils.createComment(user.getId(), item.getId(), commentDto));
    }

    @Test
    void shouldNotCreateCommentWithoutUser() {
        Mockito.when(itemStorage.findById(1)).thenReturn(Optional.ofNullable(item));
        CommentDto commentDto = CommentDto.builder()
                .id(1)
                .text("sa")
                .item("1")
                .authorName("1")
                .build();
        Assertions.assertThrows(NotFoundElementException.class, () -> utils.createComment(user.getId(), item.getId(), commentDto));
    }
}