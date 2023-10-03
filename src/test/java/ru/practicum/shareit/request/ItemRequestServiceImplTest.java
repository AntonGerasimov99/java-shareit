package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exceptions.NotFoundElementException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestStorage itemRequestStorage;
    @Mock
    private ItemStorage itemStorage;
    @Mock
    private UserStorage userStorage;
    @InjectMocks
    private ItemRequestServiceImpl service;
    private Item item;
    private User user;
    private User user2;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private LocalDateTime createdDate;

    @BeforeEach
    void beforeEach() {
        createdDate = LocalDateTime.now();
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
                .description("description")
                .available(true)
                .owner(user2)
                .build();

        itemRequest = ItemRequest.builder()
                .id(1)
                .description("description")
                .requester(user)
                .created(createdDate)
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(1)
                .description("description")
                .items(List.of(ItemMapper.toItemDTO(item)))
                .build();
    }

    @Test
    void shouldCreateRequest() {
        Mockito.when(userStorage.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRequestStorage.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto result = service.create(itemRequestDto, user.getId());

        Assertions.assertNotNull(result);
        assertThat(itemRequest.getId(), equalTo(result.getId()));
        assertThat(itemRequest.getDescription(), equalTo(result.getDescription()));
        assertThat(itemRequest.getCreated(), equalTo(result.getCreated()));

        Mockito.verify(itemRequestStorage, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void shouldFindAllByUserId() {
        Mockito.when(userStorage.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        Mockito.when(itemRequestStorage.findAllByRequesterId(user.getId())).thenReturn(List.of(itemRequest));
        Mockito.when(itemStorage.findAllByRequestId(itemRequestDto.getId())).thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = service.findAllByUserId(user.getId());

        Assertions.assertNotNull(result);
        assertThat(itemRequest.getId(), equalTo(result.get(0).getId()));
        assertThat(itemRequest.getDescription(), equalTo(result.get(0).getDescription()));

        Mockito.verify(itemRequestStorage, times(1)).findAllByRequesterId(user.getId());
    }

    @Test
    void shouldFindByUserIdAndRequestId() {
        Mockito.when(itemRequestStorage.findById(itemRequest.getRequester().getId())).thenReturn(Optional.ofNullable(itemRequest));
        Mockito.when(itemStorage.findAllByRequestId(itemRequestDto.getId())).thenReturn(Collections.emptyList());
        Mockito.when(userStorage.findById(user.getId())).thenReturn(Optional.ofNullable(user));

        ItemRequestDto result = service.findByUserIdAndRequestId(user.getId(), itemRequest.getRequester().getId());

        Assertions.assertNotNull(result);
        assertThat(itemRequest.getId(), equalTo(result.getId()));
        assertThat(itemRequest.getDescription(), equalTo(result.getDescription()));

        Mockito.verify(itemRequestStorage, times(1)).findById(itemRequest.getRequester().getId());
    }

    @Test
    void shouldFindAllByUserIdPageable() {
        Mockito.when(itemRequestStorage.findAllByRequesterIdIsNotOrderByCreatedDesc(user.getId(),
                PageRequest.of(1 / 1, 1))).thenReturn(List.of(itemRequest));
        Mockito.when(itemStorage.findAllByRequestId(itemRequestDto.getId())).thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = service.findAllPageableByUserId(user.getId(), 1, 1);

        Assertions.assertNotNull(result);
        assertThat(itemRequest.getId(), equalTo(result.get(0).getId()));
        assertThat(itemRequest.getDescription(), equalTo(result.get(0).getDescription()));

        Mockito.verify(itemRequestStorage, times(1))
                .findAllByRequesterIdIsNotOrderByCreatedDesc(user.getId(), PageRequest.of(1 / 1, 1));
    }

    @Test
    void shouldValidateRequest() {
        Mockito.when(userStorage.findById(user.getId())).thenReturn(Optional.ofNullable(user));

        ItemRequest result = service.validationRequest(itemRequestDto, user.getId());

        Assertions.assertNotNull(result);
        assertThat(itemRequest.getId(), equalTo(result.getId()));
        assertThat(itemRequest.getDescription(), equalTo(result.getDescription()));
        assertThat(itemRequest.getRequester().getId(), equalTo(result.getRequester().getId()));

        Mockito.verify(userStorage, times(1)).findById(user.getId());
    }

    @Test
    void isUser() {
        Assertions.assertThrows(NotFoundElementException.class, () -> service.isUser(15));
    }

    @Test
    void shouldAddItemsAndConvertToDto() {
        Mockito.when(itemStorage.findAllByRequestId(itemRequest.getId())).thenReturn(List.of(item));

        ItemRequestDto result = service.addItemsAndConvertToDto(itemRequest);

        Assertions.assertNotNull(result);
        assertThat(itemRequest.getId(), equalTo(result.getId()));
        assertThat(itemRequest.getDescription(), equalTo(result.getDescription()));
        assertThat(itemRequestDto.getItems().size(), equalTo(result.getItems().size()));
        assertThat(itemRequestDto.getItems().get(0).getId(), equalTo(result.getItems().get(0).getId()));

        Mockito.verify(itemStorage, times(1)).findAllByRequestId(itemRequest.getId());
    }
}