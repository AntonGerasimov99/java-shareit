package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NotFoundElementException;
import ru.practicum.shareit.item.comment.CommentStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.utils.ItemUtils;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

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
}