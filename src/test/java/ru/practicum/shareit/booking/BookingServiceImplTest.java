package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusEnum;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.booking.utils.BookingUtils;
import ru.practicum.shareit.exceptions.NotFoundElementException;
import ru.practicum.shareit.exceptions.UnknownStatusException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

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

        firstBooking.setStatus(StatusEnum.APPROVED);
        firstBookingDto.setStatus(StatusEnum.APPROVED);
        BookingDto result1 = service.updateStatus(firstBooker.getId(), firstBookingDto.getId(), true);

        assertThat(firstBooking.getId(), equalTo(result1.getId()));
        assertThat(firstBooking.getStart(), equalTo(result1.getStart()));
        assertThat(firstBooking.getEnd(), equalTo(result1.getEnd()));
        assertThat(firstBooking.getItem().getId(), equalTo(result1.getItem().getId()));
        assertThat(firstBooking.getBooker().getId(), equalTo(result1.getBooker().getId()));
        assertThat(StatusEnum.APPROVED, equalTo(result1.getStatus()));
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

    @Test
    void shouldFindAllByBooker() {
        Pageable page = PageRequest.of(1 / 1, 1);
        Mockito.when(bookingRepository.findAllByBookerIdOrderByStartDesc(firstBooker.getId(), page))
                .thenReturn(List.of(firstBooking));

        List<BookingDto> result = service.findAllByBooker(firstBooker.getId(), "ALL", 1, 1);

        assertThat(firstBooking.getId(), equalTo(result.get(0).getId()));
        assertThat(firstBooking.getStart(), equalTo(result.get(0).getStart()));
        assertThat(firstBooking.getEnd(), equalTo(result.get(0).getEnd()));
        assertThat(firstBooking.getItem().getId(), equalTo(result.get(0).getItem().getId()));
        assertThat(firstBooking.getBooker().getId(), equalTo(result.get(0).getBooker().getId()));
        assertThat(firstBooking.getStatus(), equalTo(result.get(0).getStatus()));

        firstBooking.setStatus(StatusEnum.APPROVED);
        firstBooking.setStart(LocalDateTime.now().minusMonths(1));
        firstBookingDto.setStatus(StatusEnum.APPROVED);
        firstBookingDto.setStart(LocalDateTime.now().minusMonths(1));

        Mockito.when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndIsAfterOrderByStartDesc(anyInt(),
                        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(firstBooking));
        List<BookingDto> result1 = service.findAllByBooker(firstBooker.getId(), "CURRENT", 1, 1);

        assertThat(firstBooking.getId(), equalTo(result1.get(0).getId()));
        assertThat(firstBooking.getStart(), equalTo(result1.get(0).getStart()));
        assertThat(firstBooking.getEnd(), equalTo(result1.get(0).getEnd()));
        assertThat(firstBooking.getItem().getId(), equalTo(result1.get(0).getItem().getId()));
        assertThat(firstBooking.getBooker().getId(), equalTo(result1.get(0).getBooker().getId()));
        assertThat(firstBooking.getStatus(), equalTo(result1.get(0).getStatus()));

        Mockito.verify(bookingRepository, times(1))
                .findAllByBookerIdAndStartBeforeAndEndIsAfterOrderByStartDesc(anyInt(), Mockito.any(LocalDateTime.class),
                        Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class));

        firstBooking.setEnd(LocalDateTime.now().minusDays(3));

        Mockito.when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyInt(),
                        Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(firstBooking));
        List<BookingDto> result2 = service.findAllByBooker(firstBooker.getId(), "PAST", 1, 1);

        assertThat(firstBooking.getId(), equalTo(result2.get(0).getId()));
        assertThat(firstBooking.getStart(), equalTo(result2.get(0).getStart()));
        assertThat(firstBooking.getEnd(), equalTo(result2.get(0).getEnd()));
        assertThat(firstBooking.getItem().getId(), equalTo(result2.get(0).getItem().getId()));
        assertThat(firstBooking.getBooker().getId(), equalTo(result2.get(0).getBooker().getId()));
        assertThat(firstBooking.getStatus(), equalTo(result2.get(0).getStatus()));

        Mockito.verify(bookingRepository, times(1)).findAllByBookerIdAndEndBeforeOrderByStartDesc(anyInt(),
                Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class));

        firstBooking.setStart(LocalDateTime.now().plusDays(5));
        firstBooking.setEnd(LocalDateTime.now().plusDays(10));

        Mockito.when(bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(anyInt(),
                        Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(firstBooking));
        List<BookingDto> result3 = service.findAllByBooker(firstBooker.getId(), "FUTURE", 1, 1);

        assertThat(firstBooking.getId(), equalTo(result3.get(0).getId()));
        assertThat(firstBooking.getStart(), equalTo(result3.get(0).getStart()));
        assertThat(firstBooking.getEnd(), equalTo(result3.get(0).getEnd()));
        assertThat(firstBooking.getItem().getId(), equalTo(result3.get(0).getItem().getId()));
        assertThat(firstBooking.getBooker().getId(), equalTo(result3.get(0).getBooker().getId()));
        assertThat(firstBooking.getStatus(), equalTo(result3.get(0).getStatus()));

        Mockito.verify(bookingRepository, times(1)).findAllByBookerIdAndStartIsAfterOrderByStartDesc(anyInt(),
                Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class));

        firstBooking.setStatus(StatusEnum.WAITING);

        Mockito.when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyInt(),
                        Mockito.any(StatusEnum.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(firstBooking));
        List<BookingDto> result4 = service.findAllByBooker(firstBooker.getId(), "WAITING", 1, 1);

        assertThat(firstBooking.getId(), equalTo(result4.get(0).getId()));
        assertThat(firstBooking.getStart(), equalTo(result4.get(0).getStart()));
        assertThat(firstBooking.getEnd(), equalTo(result4.get(0).getEnd()));
        assertThat(firstBooking.getItem().getId(), equalTo(result4.get(0).getItem().getId()));
        assertThat(firstBooking.getBooker().getId(), equalTo(result4.get(0).getBooker().getId()));
        assertThat(firstBooking.getStatus(), equalTo(result4.get(0).getStatus()));

        Mockito.verify(bookingRepository, times(1)).findAllByBookerIdAndStatusOrderByStartDesc(anyInt(),
                Mockito.any(StatusEnum.class), Mockito.any(Pageable.class));

        firstBooking.setStatus(StatusEnum.REJECTED);

        Mockito.when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyInt(),
                        Mockito.any(StatusEnum.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(firstBooking));
        List<BookingDto> result5 = service.findAllByBooker(firstBooker.getId(), "REJECTED", 1, 1);

        assertThat(firstBooking.getId(), equalTo(result5.get(0).getId()));
        assertThat(firstBooking.getStart(), equalTo(result5.get(0).getStart()));
        assertThat(firstBooking.getEnd(), equalTo(result5.get(0).getEnd()));
        assertThat(firstBooking.getItem().getId(), equalTo(result5.get(0).getItem().getId()));
        assertThat(firstBooking.getBooker().getId(), equalTo(result5.get(0).getBooker().getId()));
        assertThat(firstBooking.getStatus(), equalTo(result5.get(0).getStatus()));

        Assertions.assertThrows(UnknownStatusException.class, () -> service.findAllByBooker(1, "FROM", 1, 1));
    }

    @Test
    void shouldFindAllByOwner() {
        Pageable page = PageRequest.of(1 / 1, 1);
        Mockito.when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(firstOwner.getId(), page))
                .thenReturn(List.of(firstBooking));

        List<BookingDto> result = service.findAllByOwner(firstOwner.getId(), "ALL", 1, 1);

        assertThat(firstBooking.getId(), equalTo(result.get(0).getId()));
        assertThat(firstBooking.getStart(), equalTo(result.get(0).getStart()));
        assertThat(firstBooking.getEnd(), equalTo(result.get(0).getEnd()));
        assertThat(firstBooking.getItem().getId(), equalTo(result.get(0).getItem().getId()));
        assertThat(firstBooking.getBooker().getId(), equalTo(result.get(0).getBooker().getId()));
        assertThat(firstBooking.getStatus(), equalTo(result.get(0).getStatus()));

        firstBooking.setStatus(StatusEnum.APPROVED);
        firstBooking.setStart(LocalDateTime.now().minusMonths(1));
        firstBookingDto.setStatus(StatusEnum.APPROVED);
        firstBookingDto.setStart(LocalDateTime.now().minusMonths(1));

        Mockito.when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndIsAfterOrderByStartDesc(anyInt(),
                        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(firstBooking));
        List<BookingDto> result1 = service.findAllByOwner(firstOwner.getId(), "CURRENT", 1, 1);

        assertThat(firstBooking.getId(), equalTo(result1.get(0).getId()));
        assertThat(firstBooking.getStart(), equalTo(result1.get(0).getStart()));
        assertThat(firstBooking.getEnd(), equalTo(result1.get(0).getEnd()));
        assertThat(firstBooking.getItem().getId(), equalTo(result1.get(0).getItem().getId()));
        assertThat(firstBooking.getBooker().getId(), equalTo(result1.get(0).getBooker().getId()));
        assertThat(firstBooking.getStatus(), equalTo(result1.get(0).getStatus()));

        Mockito.verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStartBeforeAndEndIsAfterOrderByStartDesc(anyInt(), Mockito.any(LocalDateTime.class),
                        Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class));

        firstBooking.setEnd(LocalDateTime.now().minusDays(3));

        Mockito.when(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(anyInt(),
                        Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(firstBooking));
        List<BookingDto> result2 = service.findAllByOwner(firstOwner.getId(), "PAST", 1, 1);

        assertThat(firstBooking.getId(), equalTo(result2.get(0).getId()));
        assertThat(firstBooking.getStart(), equalTo(result2.get(0).getStart()));
        assertThat(firstBooking.getEnd(), equalTo(result2.get(0).getEnd()));
        assertThat(firstBooking.getItem().getId(), equalTo(result2.get(0).getItem().getId()));
        assertThat(firstBooking.getBooker().getId(), equalTo(result2.get(0).getBooker().getId()));
        assertThat(firstBooking.getStatus(), equalTo(result2.get(0).getStatus()));

        Mockito.verify(bookingRepository, times(1)).findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(anyInt(),
                Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class));

        firstBooking.setStart(LocalDateTime.now().plusDays(5));
        firstBooking.setEnd(LocalDateTime.now().plusDays(10));

        Mockito.when(bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(anyInt(),
                        Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(firstBooking));
        List<BookingDto> result3 = service.findAllByOwner(firstOwner.getId(), "FUTURE", 1, 1);

        assertThat(firstBooking.getId(), equalTo(result3.get(0).getId()));
        assertThat(firstBooking.getStart(), equalTo(result3.get(0).getStart()));
        assertThat(firstBooking.getEnd(), equalTo(result3.get(0).getEnd()));
        assertThat(firstBooking.getItem().getId(), equalTo(result3.get(0).getItem().getId()));
        assertThat(firstBooking.getBooker().getId(), equalTo(result3.get(0).getBooker().getId()));
        assertThat(firstBooking.getStatus(), equalTo(result3.get(0).getStatus()));

        Mockito.verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(anyInt(),
                Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class));

        firstBooking.setStatus(StatusEnum.WAITING);

        Mockito.when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(anyInt(),
                        Mockito.any(StatusEnum.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(firstBooking));
        List<BookingDto> result4 = service.findAllByOwner(firstOwner.getId(), "WAITING", 1, 1);

        assertThat(firstBooking.getId(), equalTo(result4.get(0).getId()));
        assertThat(firstBooking.getStart(), equalTo(result4.get(0).getStart()));
        assertThat(firstBooking.getEnd(), equalTo(result4.get(0).getEnd()));
        assertThat(firstBooking.getItem().getId(), equalTo(result4.get(0).getItem().getId()));
        assertThat(firstBooking.getBooker().getId(), equalTo(result4.get(0).getBooker().getId()));
        assertThat(firstBooking.getStatus(), equalTo(result4.get(0).getStatus()));

        Mockito.verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStatusOrderByStartDesc(anyInt(),
                Mockito.any(StatusEnum.class), Mockito.any(Pageable.class));

        firstBooking.setStatus(StatusEnum.REJECTED);

        Mockito.when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(anyInt(),
                        Mockito.any(StatusEnum.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(firstBooking));
        List<BookingDto> result5 = service.findAllByOwner(firstOwner.getId(), "REJECTED", 1, 1);

        assertThat(firstBooking.getId(), equalTo(result5.get(0).getId()));
        assertThat(firstBooking.getStart(), equalTo(result5.get(0).getStart()));
        assertThat(firstBooking.getEnd(), equalTo(result5.get(0).getEnd()));
        assertThat(firstBooking.getItem().getId(), equalTo(result5.get(0).getItem().getId()));
        assertThat(firstBooking.getBooker().getId(), equalTo(result5.get(0).getBooker().getId()));
        assertThat(firstBooking.getStatus(), equalTo(result5.get(0).getStatus()));

        Assertions.assertThrows(UnknownStatusException.class, () -> service.findAllByOwner(1, "FROM", 1, 1));
    }
}