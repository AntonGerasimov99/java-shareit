package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.StatusEnum;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingStorage extends JpaRepository<Booking, Integer> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Integer bookerId);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndIsAfterOrderByStartDesc(Integer bookerId, LocalDateTime start,
                                                                               LocalDateTime end);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Integer bookerId, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Integer bookerId, LocalDateTime start);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Integer bookerId, StatusEnum status);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Integer ownerId);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndIsAfterOrderByStartDesc(Integer ownerId,
                                                                                  LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Integer ownerId, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(Integer ownerId, LocalDateTime start);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Integer ownerId, StatusEnum status);

    // last bookings
    Optional<Booking> findFirstByItemIdAndStartIsBeforeOrderByStartDesc(Integer itemId, LocalDateTime time);

    // next bookings
    Optional<Booking> findFirstByItemIdAndStartIsAfterAndStatusOrderByStartAsc(Integer itemId, LocalDateTime time,
                                                                               StatusEnum status);

}