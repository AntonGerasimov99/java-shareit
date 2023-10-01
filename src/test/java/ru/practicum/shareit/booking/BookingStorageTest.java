package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusEnum;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
public class BookingStorageTest {

    @Autowired
    private BookingStorage bookingStorage;
    @Autowired
    private ItemStorage itemStorage;
    @Autowired
    private UserStorage userStorage;

    private User firstOwner;
    private User secondOwner;
    private User firstBooker;
    private User secondBooker;
    private Item firstItem;
    private Item secondItem;
    private Booking firstBooking;
    private Booking secondBooking;
    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void beforeEach() {
        firstOwner = User.builder().name("firstOwner").email("firstOwner@mail.ru").build();
        secondOwner = User.builder().name("secondOwner").email("secondOwner@mail.ru").build();
        firstBooker = User.builder().name("firstBooker").email("firstBooker@mail.ru").build();
        secondBooker = User.builder().name("secondBooker").email("secondBooker@mail.ru").build();
        firstItem = Item.builder().name("firstItem").description("firstItem description").available(true)
                .owner(firstOwner).build();
        secondItem = Item.builder().name("secondItem").description("secondItem description").available(true)
                .owner(secondOwner).build();

        firstOwner = userStorage.save(firstOwner);
        secondOwner = userStorage.save(secondOwner);
        firstBooker = userStorage.save(firstBooker);
        secondBooker = userStorage.save(secondBooker);

        firstItem = itemStorage.save(firstItem);
        secondItem = itemStorage.save(secondItem);

        start = LocalDateTime.now();
        end = LocalDateTime.now().plusHours(1);

        firstBooking = Booking.builder().id(1).start(start).end(end).item(firstItem).booker(firstBooker)
                .status(StatusEnum.WAITING).build();
        secondBooking = Booking.builder().id(2).start(start).end(end).item(secondItem).booker(secondBooker)
                .status(StatusEnum.APPROVED).build();

        firstBooking = bookingStorage.save(firstBooking);
        secondBooking = bookingStorage.save(secondBooking);
    }

    @Test
    void shouldFindAllByBookerId() {
        List<Booking> result = bookingStorage.findAllByBookerIdOrderByStartDesc(firstBooker.getId(), Pageable.unpaged());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        assertThat(firstBooking.getId(), equalTo(result.get(0).getId()));
        assertThat(firstBooking.getStart(), equalTo(result.get(0).getStart()));
        assertThat(firstBooking.getEnd(), equalTo(result.get(0).getEnd()));
        assertThat(firstBooking.getItem().getId(), equalTo(result.get(0).getItem().getId()));
        assertThat(firstBooking.getBooker().getId(), equalTo(result.get(0).getBooker().getId()));
        assertThat(firstBooking.getStatus(), equalTo(result.get(0).getStatus()));
    }

    @Test
    void shouldFindByBookerIdAndStartAndEnd() {
        List<Booking> result = bookingStorage.findAllByBookerIdAndStartBeforeAndEndIsAfterOrderByStartDesc(firstBooker.getId(),
                start.plusHours(1), end.minusHours(1), Pageable.unpaged());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        assertThat(firstBooking.getId(), equalTo(result.get(0).getId()));
        assertThat(firstBooking.getStart(), equalTo(result.get(0).getStart()));
        assertThat(firstBooking.getEnd(), equalTo(result.get(0).getEnd()));
        assertThat(firstBooking.getItem().getId(), equalTo(result.get(0).getItem().getId()));
        assertThat(firstBooking.getBooker().getId(), equalTo(result.get(0).getBooker().getId()));
        assertThat(firstBooking.getStatus(), equalTo(result.get(0).getStatus()));
    }

    @Test
    void shouldFindByBookerIdAndEndBefore() {
        List<Booking> result = bookingStorage.findAllByBookerIdAndEndBeforeOrderByStartDesc(firstBooker.getId(),
                end.plusDays(1), Pageable.unpaged());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        assertThat(firstBooking.getId(), equalTo(result.get(0).getId()));
        assertThat(firstBooking.getStart(), equalTo(result.get(0).getStart()));
        assertThat(firstBooking.getEnd(), equalTo(result.get(0).getEnd()));
        assertThat(firstBooking.getItem().getId(), equalTo(result.get(0).getItem().getId()));
        assertThat(firstBooking.getBooker().getId(), equalTo(result.get(0).getBooker().getId()));
        assertThat(firstBooking.getStatus(), equalTo(result.get(0).getStatus()));
    }

    @Test
    void shouldFindByBookerIdAndStartAfter() {
        List<Booking> result = bookingStorage.findAllByBookerIdAndStartIsAfterOrderByStartDesc(firstBooker.getId(),
                start.minusDays(1), Pageable.unpaged());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        assertThat(firstBooking.getId(), equalTo(result.get(0).getId()));
        assertThat(firstBooking.getStart(), equalTo(result.get(0).getStart()));
        assertThat(firstBooking.getEnd(), equalTo(result.get(0).getEnd()));
        assertThat(firstBooking.getItem().getId(), equalTo(result.get(0).getItem().getId()));
        assertThat(firstBooking.getBooker().getId(), equalTo(result.get(0).getBooker().getId()));
        assertThat(firstBooking.getStatus(), equalTo(result.get(0).getStatus()));
    }

    @Test
    void shouldFindByBookerIdAndStatus() {
        List<Booking> result = bookingStorage.findAllByBookerIdAndStatusOrderByStartDesc(firstBooker.getId(),
                StatusEnum.WAITING, Pageable.unpaged());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        assertThat(firstBooking.getId(), equalTo(result.get(0).getId()));
        assertThat(firstBooking.getStart(), equalTo(result.get(0).getStart()));
        assertThat(firstBooking.getEnd(), equalTo(result.get(0).getEnd()));
        assertThat(firstBooking.getItem().getId(), equalTo(result.get(0).getItem().getId()));
        assertThat(firstBooking.getBooker().getId(), equalTo(result.get(0).getBooker().getId()));
        assertThat(firstBooking.getStatus(), equalTo(result.get(0).getStatus()));
    }

    @Test
    void shouldFindByItemOwnerId() {
        List<Booking> result = bookingStorage.findAllByItemOwnerIdOrderByStartDesc(firstOwner.getId(), Pageable.unpaged());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        assertThat(firstBooking.getId(), equalTo(result.get(0).getId()));
        assertThat(firstBooking.getStart(), equalTo(result.get(0).getStart()));
        assertThat(firstBooking.getEnd(), equalTo(result.get(0).getEnd()));
        assertThat(firstBooking.getItem().getId(), equalTo(result.get(0).getItem().getId()));
        assertThat(firstBooking.getBooker().getId(), equalTo(result.get(0).getBooker().getId()));
        assertThat(firstBooking.getStatus(), equalTo(result.get(0).getStatus()));
    }

    @Test
    void shouldFindByItemOwnerIdAndStartBeforeAndEndIsAfter() {
        List<Booking> result = bookingStorage.findAllByItemOwnerIdAndStartBeforeAndEndIsAfterOrderByStartDesc(firstOwner.getId(),
                start.plusHours(1), end.minusHours(1), Pageable.unpaged());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        assertThat(firstBooking.getId(), equalTo(result.get(0).getId()));
        assertThat(firstBooking.getStart(), equalTo(result.get(0).getStart()));
        assertThat(firstBooking.getEnd(), equalTo(result.get(0).getEnd()));
        assertThat(firstBooking.getItem().getId(), equalTo(result.get(0).getItem().getId()));
        assertThat(firstBooking.getBooker().getId(), equalTo(result.get(0).getBooker().getId()));
        assertThat(firstBooking.getStatus(), equalTo(result.get(0).getStatus()));
    }

    @Test
    void shouldFindByItemOwnerIdAndEndBefore() {
        List<Booking> result = bookingStorage.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(firstOwner.getId(),
                end.plusDays(1), Pageable.unpaged());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        assertThat(firstBooking.getId(), equalTo(result.get(0).getId()));
        assertThat(firstBooking.getStart(), equalTo(result.get(0).getStart()));
        assertThat(firstBooking.getEnd(), equalTo(result.get(0).getEnd()));
        assertThat(firstBooking.getItem().getId(), equalTo(result.get(0).getItem().getId()));
        assertThat(firstBooking.getBooker().getId(), equalTo(result.get(0).getBooker().getId()));
        assertThat(firstBooking.getStatus(), equalTo(result.get(0).getStatus()));
    }

    @Test
    void shouldFindByItemOwnerIdAndStartIsAfter() {
        List<Booking> result = bookingStorage.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(firstOwner.getId(),
                start.minusDays(1), Pageable.unpaged());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        assertThat(firstBooking.getId(), equalTo(result.get(0).getId()));
        assertThat(firstBooking.getStart(), equalTo(result.get(0).getStart()));
        assertThat(firstBooking.getEnd(), equalTo(result.get(0).getEnd()));
        assertThat(firstBooking.getItem().getId(), equalTo(result.get(0).getItem().getId()));
        assertThat(firstBooking.getBooker().getId(), equalTo(result.get(0).getBooker().getId()));
        assertThat(firstBooking.getStatus(), equalTo(result.get(0).getStatus()));
    }

    @Test
    void shouldFindByItemOwnerIdAndStatus() {
        List<Booking> result = bookingStorage.findAllByItemOwnerIdAndStatusOrderByStartDesc(firstOwner.getId(),
                StatusEnum.WAITING, Pageable.unpaged());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        assertThat(firstBooking.getId(), equalTo(result.get(0).getId()));
        assertThat(firstBooking.getStart(), equalTo(result.get(0).getStart()));
        assertThat(firstBooking.getEnd(), equalTo(result.get(0).getEnd()));
        assertThat(firstBooking.getItem().getId(), equalTo(result.get(0).getItem().getId()));
        assertThat(firstBooking.getBooker().getId(), equalTo(result.get(0).getBooker().getId()));
        assertThat(firstBooking.getStatus(), equalTo(result.get(0).getStatus()));
    }

    @Test
    void shouldFindByItemIdAndStartIsBefore() {
        Optional<Booking> result = bookingStorage.findFirstByItemIdAndStartIsBeforeOrderByStartDesc(firstBooking.getItem().getId(),
                LocalDateTime.now());

        Assertions.assertNotNull(result);
        assertThat(firstBooking.getId(), equalTo(result.get().getId()));
        assertThat(firstBooking.getStart(), equalTo(result.get().getStart()));
        assertThat(firstBooking.getEnd(), equalTo(result.get().getEnd()));
        assertThat(firstBooking.getItem().getId(), equalTo(result.get().getItem().getId()));
        assertThat(firstBooking.getBooker().getId(), equalTo(result.get().getBooker().getId()));
        assertThat(firstBooking.getStatus(), equalTo(result.get().getStatus()));
    }

    @Test
    void shouldFindByItemIdAndStartIsAfterAndStatus() {
        Optional<Booking> result = bookingStorage.findFirstByItemIdAndStartIsAfterAndStatusOrderByStartAsc(firstBooking.getItem().getId(),
                start.minusDays(1), StatusEnum.WAITING);

        Assertions.assertNotNull(result);
        assertThat(firstBooking.getId(), equalTo(result.get().getId()));
        assertThat(firstBooking.getStart(), equalTo(result.get().getStart()));
        assertThat(firstBooking.getEnd(), equalTo(result.get().getEnd()));
        assertThat(firstBooking.getItem().getId(), equalTo(result.get().getItem().getId()));
        assertThat(firstBooking.getBooker().getId(), equalTo(result.get().getBooker().getId()));
        assertThat(firstBooking.getStatus(), equalTo(result.get().getStatus()));
    }
}
