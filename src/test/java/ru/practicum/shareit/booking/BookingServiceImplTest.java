package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.booking.utils.BookingUtils;
import ru.practicum.shareit.exceptions.NotFoundElementException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    private BookingStorage bookingRepository;
    @Mock
    private BookingUtils utils;
    @InjectMocks
    private BookingServiceImpl service;

    private User firstOwner;
    private User secondOwner;
    private User firstBooker;
    private UserDto firstBookerDto;
    private User secondBooker;
    private Item firstItem;
    private ItemDto firstItemDto;
    private Item secondItem;
    private BookingDto firstBookingDto;
    private Booking firstBooking;
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

        firstBooking = Booking.builder().id(1).start(start).end(end).item(firstItem).booker(firstBooker)
                .status(StatusEnum.WAITING).build();
        firstBookingDto = BookingDto.builder().id(1).start(start).end(end).item(firstItemDto).booker(firstBookerDto)
                .status(StatusEnum.WAITING).itemId(1).build();
        secondBooking = Booking.builder().id(2).start(start).end(end).item(secondItem).booker(secondBooker)
                .status(StatusEnum.WAITING).build();
    }

    @Test
    void shouldCreate() {
        Mockito.when(utils.validation(firstBooker.getId(), firstBookingDto)).thenReturn(firstBooking);
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(firstBooking);

        BookingDto result = service.create(firstBooker.getId(), firstBookingDto);

        assertThat(firstBooking.getId(), equalTo(result.getId()));
        assertThat(firstBooking.getStart(), equalTo(result.getStart()));
        assertThat(firstBooking.getEnd(), equalTo(result.getEnd()));
        assertThat(firstBooking.getItem().getId(), equalTo(result.getItem().getId()));
        assertThat(firstBooking.getBooker().getId(), equalTo(result.getBooker().getId()));
        assertThat(firstBooking.getStatus(), equalTo(result.getStatus()));
    }

    @Test
    void shouldUpdateStatus() {
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(firstBooking);
        Mockito.when(bookingRepository.findById(firstBooking.getId())).thenReturn(Optional.ofNullable(firstBooking));

        firstBooking.setStatus(StatusEnum.REJECTED);
        firstBookingDto.setStatus(StatusEnum.REJECTED);
        BookingDto result = service.updateStatus(firstBooker.getId(), firstBookingDto.getId(), true);

        assertThat(firstBooking.getId(), equalTo(result.getId()));
        assertThat(firstBooking.getStart(), equalTo(result.getStart()));
        assertThat(firstBooking.getEnd(), equalTo(result.getEnd()));
        assertThat(firstBooking.getItem().getId(), equalTo(result.getItem().getId()));
        assertThat(firstBooking.getBooker().getId(), equalTo(result.getBooker().getId()));
        assertThat(StatusEnum.APPROVED, equalTo(result.getStatus()));
    }

    @Test
    void shouldGetByOwner() {
        Mockito.when(bookingRepository.findById(firstBooking.getId())).thenReturn(Optional.ofNullable(firstBooking));

        BookingDto result = service.get(firstBooker.getId(), firstBookingDto.getId());

        assertThat(firstBooking.getId(), equalTo(result.getId()));
        assertThat(firstBooking.getStart(), equalTo(result.getStart()));
        assertThat(firstBooking.getEnd(), equalTo(result.getEnd()));
        assertThat(firstBooking.getItem().getId(), equalTo(result.getItem().getId()));
        assertThat(firstBooking.getBooker().getId(), equalTo(result.getBooker().getId()));
        assertThat(firstBooking.getStatus(), equalTo(result.getStatus()));
    }

    @Test
    void shouldGetByBookingId() {
        Assertions.assertThrows(NotFoundElementException.class, () -> service.getByBookingId(15));

        Mockito.when(bookingRepository.findById(firstBooking.getId())).thenReturn(Optional.ofNullable(firstBooking));

        Booking result = service.getByBookingId(firstBooking.getId());

        assertThat(firstBooking.getId(), equalTo(result.getId()));
        assertThat(firstBooking.getStart(), equalTo(result.getStart()));
        assertThat(firstBooking.getEnd(), equalTo(result.getEnd()));
        assertThat(firstBooking.getItem().getId(), equalTo(result.getItem().getId()));
        assertThat(firstBooking.getBooker().getId(), equalTo(result.getBooker().getId()));
        assertThat(firstBooking.getStatus(), equalTo(result.getStatus()));
    }
}