package ru.practicum.shareit.booking;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusEnum;
import ru.practicum.shareit.booking.utils.BookingUtils;
import ru.practicum.shareit.exceptions.NotFoundElementException;
import ru.practicum.shareit.exceptions.ValidationElementException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;

@ExtendWith(MockitoExtension.class)
public class BookingUtilsTest {

    @Mock
    private UserStorage userStorage;
    @Mock
    private ItemStorage itemStorage;
    @InjectMocks
    private BookingUtils utils;

    private User firstOwner;
    private User secondOwner;
    private User firstBooker;
    private UserDto firstBookerDto;
    private User secondBooker;
    private Item firstItem;
    private ItemDto firstItemDto;
    private Item secondItem;
    private BookingDto firstBookingDto;
    private Booking secondBooking;
    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void beforeEach() {
        firstOwner = User.builder().id(1).name("firstOwner").email("firstOwner@mail.ru").build();
        secondOwner = User.builder().id(2).name("secondOwner").email("secondOwner@mail.ru").build();
        firstBooker = User.builder().id(3).name("firstBooker").email("firstBooker@mail.ru").build();
        secondBooker = User.builder().id(4).name("secondBooker").email("secondBooker@mail.ru").build();
        firstItem = Item.builder().id(1).name("firstItem").description("firstItem description").available(true)
                .owner(firstOwner).build();
        secondItem = Item.builder().id(2).name("secondItem").description("secondItem description").available(true)
                .owner(secondOwner).build();

        start = LocalDateTime.now().plusDays(1);
        end = LocalDateTime.now().plusDays(2);
        firstItemDto = ItemMapper.toItemDTO(firstItem);


        firstBookingDto = BookingDto.builder().id(1).start(start).end(end).item(firstItemDto).booker(firstBookerDto)
                .status(StatusEnum.WAITING).itemId(1).build();
        secondBooking = Booking.builder().id(2).start(start).end(end).item(secondItem).booker(secondBooker)
                .status(StatusEnum.WAITING).build();
    }

    @Test
    void shouldValidate() {
        Mockito.when(userStorage.findById(firstBooker.getId())).thenReturn(Optional.ofNullable(firstBooker));
        Mockito.when(userStorage.findById(firstOwner.getId())).thenReturn(Optional.ofNullable(firstOwner));
        Mockito.when(itemStorage.findById(firstItem.getId())).thenReturn(Optional.ofNullable(firstItem));

        Assertions.assertThrows(NotFoundElementException.class, () -> utils.validation(firstOwner.getId(), firstBookingDto));

        firstItem.setAvailable(false);
        Assertions.assertThrows(ValidationElementException.class, () -> utils.validation(firstBooker.getId(), firstBookingDto));

        firstItem.setAvailable(true);
        Booking result = utils.validation(firstBooker.getId(), firstBookingDto);

        MatcherAssert.assertThat(result.getId(), equalTo(firstBookingDto.getId()));
        MatcherAssert.assertThat(result.getStart(), equalTo(firstBookingDto.getStart()));
        MatcherAssert.assertThat(result.getEnd(), equalTo(firstBookingDto.getEnd()));
        MatcherAssert.assertThat(result.getItem().getId(), equalTo(firstBookingDto.getItem().getId()));
        MatcherAssert.assertThat(result.getStatus(), equalTo(firstBookingDto.getStatus()));
    }

    @Test
    void shouldValidateDate() {
        LocalDateTime wrongStart = LocalDateTime.of(2024, 2, 12, 15, 2);
        LocalDateTime endEqualStart = LocalDateTime.of(2024, 2, 12, 15, 2);
        LocalDateTime startAfterEnd = LocalDateTime.of(2025, 2, 12, 15, 2);
        LocalDateTime endBeforeStart = LocalDateTime.now().minusDays(1);

        secondBooking.setStart(null);
        Assertions.assertThrows(ValidationElementException.class, () -> utils.validationDate(secondBooking));

        secondBooking.setStart(wrongStart);
        secondBooking.setEnd(endEqualStart);
        Assertions.assertThrows(ValidationElementException.class, () -> utils.validationDate(secondBooking));

        secondBooking.setStart(startAfterEnd);
        Assertions.assertThrows(ValidationElementException.class, () -> utils.validationDate(secondBooking));

        secondBooking.setStart(LocalDateTime.now());
        secondBooking.setEnd(endBeforeStart);
        Assertions.assertThrows(ValidationElementException.class, () -> utils.validationDate(secondBooking));
    }

    @Test
    void shouldCheckIsUserOwnerOrBooker() {
        Mockito.when(itemStorage.getById(secondBooking.getItem().getId())).thenReturn(secondItem);
        Assertions.assertThrows(NotFoundElementException.class, () -> utils.isOwnerOrBooker(firstBooker.getId(), secondBooking));
    }

    @Test
    void shouldCheckIsUserOwner() {
        Mockito.when(itemStorage.getById(secondBooking.getItem().getId())).thenReturn(secondItem);
        Assertions.assertThrows(NotFoundElementException.class, () -> utils.isOwner(firstBooker.getId(), secondBooking));
    }

    @Test
    void shouldCheckIsUser() {
        Assertions.assertThrows(NotFoundElementException.class, () -> utils.isUser(66));
    }

    @Test
    void shouldCheckIsApprove() {
        secondBooking.setStatus(StatusEnum.APPROVED);
        Assertions.assertThrows(ValidationElementException.class, () -> utils.isApprove(secondBooking, true));

        secondBooking.setStatus(StatusEnum.REJECTED);
        Assertions.assertThrows(ValidationElementException.class, () -> utils.isApprove(secondBooking, true));
    }
}
