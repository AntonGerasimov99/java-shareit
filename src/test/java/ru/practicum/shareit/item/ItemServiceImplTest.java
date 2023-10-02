package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusEnum;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NotFoundElementException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.utils.ItemUtils;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemStorage itemStorage;
    @Mock
    private UserStorage userStorage;
    @Mock
    private CommentStorage commentStorage;
    @Mock
    private ItemUtils itemUtils;
    @Mock
    private BookingService bookingService;
    @Mock
    private ItemRequestStorage itemRequestStorage;
    @InjectMocks
    private ItemServiceImpl itemService;
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
    void shouldCreateItem() {
        Mockito.when(userStorage.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        Mockito.when(itemStorage.findById(itemDto.getId())).thenReturn(Optional.ofNullable(item));

        ItemDto result = itemService.create(itemDto, user.getId());

        assertThat(itemDto.getId(), equalTo(result.getId()));
        assertThat(itemDto.getName(), equalTo(result.getName()));
        assertThat(itemDto.getDescription(), equalTo(result.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(result.getAvailable()));

        Mockito.verify(userStorage, times(1)).findById(user.getId());
    }

    @Test
    void shouldGetItem() {
        Mockito.when(itemStorage.findById(itemDto.getId())).thenReturn(Optional.ofNullable(item));

        ItemDto result = itemService.get(itemDto.getId());

        assertThat(itemDto.getId(), equalTo(result.getId()));
        assertThat(itemDto.getName(), equalTo(result.getName()));
        assertThat(itemDto.getDescription(), equalTo(result.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(result.getAvailable()));

        Mockito.verify(itemStorage, times(1)).findById(itemDto.getId());

        Assertions.assertThrows(NotFoundElementException.class, () -> itemService.get(15));
    }

    @Test
    void shouldGetItemByItemIdAndUserId() {
        Mockito.when(itemStorage.findById(itemDto.getId())).thenReturn(Optional.ofNullable(item));
        Mockito.when(userStorage.findById(user.getId())).thenReturn(Optional.ofNullable(user));

        ItemDto result = itemService.getItem(itemDto.getId(), user.getId());

        assertThat(itemDto.getId(), equalTo(result.getId()));
        assertThat(itemDto.getName(), equalTo(result.getName()));
        assertThat(itemDto.getDescription(), equalTo(result.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(result.getAvailable()));

        Mockito.verify(itemStorage, times(1)).findById(itemDto.getId());

        Assertions.assertThrows(NotFoundElementException.class, () -> itemService.getItem(itemDto.getId(), 15));
        Assertions.assertThrows(NotFoundElementException.class, () -> itemService.getItem(15, user.getId()));
    }

    @Test
    void shouldUpdateItem() {
        ItemDto newItemDto = ItemDto.builder()
                .id(1)
                .build();

        Mockito.when(itemStorage.findById(itemDto.getId())).thenReturn(Optional.ofNullable(item));
        Mockito.when(userStorage.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        Mockito.when(itemStorage.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.update(newItemDto, user.getId());

        assertThat(itemDto.getId(), equalTo(result.getId()));
        assertThat(itemDto.getName(), equalTo(result.getName()));
        assertThat(itemDto.getDescription(), equalTo(result.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(result.getAvailable()));

        Mockito.verify(itemStorage, times(1)).findById(itemDto.getId());

        Assertions.assertThrows(NotFoundElementException.class, () -> itemService.getItem(itemDto.getId(), 15));
        Assertions.assertThrows(NotFoundElementException.class, () -> itemService.getItem(15, user.getId()));
    }

    @Test
    void shouldGetAllItemsByUserId() {
        User user2 = User.builder()
                .id(1)
                .name("name2")
                .email("new2@mail.ru")
                .build();

        Comment comment = Comment.builder()
                .id(1)
                .text("text")
                .item(item)
                .author(user2)
                .date(LocalDateTime.now())
                .build();

        Mockito.when(itemStorage.findAllByOwnerId(user.getId(), PageRequest.of(1 / 1, 1))).thenReturn(List.of(item));
        Mockito.when(commentStorage.findAllByItemIdOrderByDate(itemDto.getId())).thenReturn(List.of(comment));
        Mockito.when(bookingService.getLastBooking(itemDto.getId())).thenReturn(null);
        Mockito.when(bookingService.getNextBooking(itemDto.getId())).thenReturn(null);

        List<ItemDto> result = itemService.getAllItemsByUserId(user.getId(), 1, 1);

        assertThat(itemDto.getId(), equalTo(result.get(0).getId()));
        assertThat(itemDto.getName(), equalTo(result.get(0).getName()));
        assertThat(itemDto.getDescription(), equalTo(result.get(0).getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(result.get(0).getAvailable()));

        Mockito.verify(itemStorage, times(1)).findAllByOwnerId(user.getId(), PageRequest.of(1 / 1, 1));
    }

    @Test
    void shouldSearch() {
        Mockito.when(itemStorage.findAllByDescriptionContainsIgnoreCase("any", PageRequest.of(1 / 1, 1)))
                .thenReturn(List.of(item));

        List<ItemDto> result = itemService.search("any", 1, 1);

        assertThat(itemDto.getId(), equalTo(result.get(0).getId()));
        assertThat(itemDto.getName(), equalTo(result.get(0).getName()));
        assertThat(itemDto.getDescription(), equalTo(result.get(0).getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(result.get(0).getAvailable()));

        Mockito.verify(itemStorage, times(1)).findAllByDescriptionContainsIgnoreCase("any",
                PageRequest.of(1 / 1, 1));
    }

    @Test
    void shouldNotSearchWithEmptyText() {
        List<ItemDto> result = itemService.search(null, 1, 1);

        assertThat(result.size(), equalTo(0));
    }

    @Test
    void shouldCreateComment() {
        User user2 = User.builder()
                .id(1)
                .name("name2")
                .email("new2@mail.ru")
                .build();

        Comment comment = Comment.builder()
                .id(1)
                .text("text")
                .item(item)
                .author(user2)
                .date(LocalDateTime.now())
                .build();

        CommentDto check = CommentMapper.toCommentDto(comment);

        Mockito.when(itemUtils.createComment(user2.getId(), item.getId(), CommentMapper.toCommentDto(comment)))
                .thenReturn(comment);
        Mockito.when(commentStorage.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = itemService.createComment(user2.getId(), item.getId(), CommentMapper.toCommentDto(comment));

        assertThat(check.getId(), equalTo(result.getId()));
        assertThat(check.getText(), equalTo(result.getText()));
        assertThat(check.getItem(), equalTo(result.getItem()));
        assertThat(check.getAuthorName(), equalTo(result.getAuthorName()));
    }

    @Test
    void shouldUpdateComment() {
        User user2 = User.builder()
                .id(1)
                .name("name2")
                .email("new2@mail.ru")
                .build();

        Comment comment = Comment.builder()
                .id(1)
                .text("text")
                .item(item)
                .author(user2)
                .date(LocalDateTime.now())
                .build();

        CommentDto check = CommentMapper.toCommentDto(comment);

        Mockito.when(itemUtils.createComment(user2.getId(), item.getId(), CommentMapper.toCommentDto(comment)))
                .thenReturn(comment);
        Mockito.when(commentStorage.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = itemService.createComment(user2.getId(), item.getId(), CommentMapper.toCommentDto(comment));

        assertThat(check.getId(), equalTo(result.getId()));
        assertThat(check.getText(), equalTo(result.getText()));
        assertThat(check.getItem(), equalTo(result.getItem()));
        assertThat(check.getAuthorName(), equalTo(result.getAuthorName()));
    }

    @Test
    void shouldUpdateBookings() {
        User firstOwner = User.builder().id(1).name("firstOwner").email("firstOwner@mail.ru").build();
        User firstBooker = User.builder().id(3).name("firstBooker").email("firstBooker@mail.ru").build();
        User secondBooker = User.builder().id(4).name("secondBooker").email("secondBooker@mail.ru").build();
        Item firstItem = Item.builder().id(1).name("firstItem").description("firstItem description").available(true)
                .owner(firstOwner).build();

        LocalDateTime start = LocalDateTime.now().minusDays(5);
        LocalDateTime end = LocalDateTime.now().minusDays(4);
        ItemDto firstItemDto = ItemMapper.toItemDTO(firstItem);

        Booking firstBooking = Booking.builder().id(1).start(start).end(end).item(firstItem).booker(firstBooker)
                .status(StatusEnum.CANCELED).build();
        Booking secondBooking = Booking.builder().id(2).start(start.plusDays(6)).end(end.plusDays(8)).item(firstItem).booker(secondBooker)
                .status(StatusEnum.WAITING).build();

        Mockito.when(bookingService.getLastBooking(firstItem.getId())).thenReturn(firstBooking);
        Mockito.when(bookingService.getNextBooking(firstItem.getId())).thenReturn(secondBooking);

        ItemDto result = itemService.updateBookings(firstItemDto);

        assertThat(result.getLastBooking().getId(), equalTo(firstBooking.getId()));
        assertThat(result.getLastBooking().getBookerId(), equalTo(firstBooking.getBooker().getId()));
        assertThat(result.getLastBooking().getId(), equalTo(firstBooking.getId()));
        assertThat(result.getNextBooking().getBookerId(), equalTo(secondBooking.getBooker().getId()));
    }
}