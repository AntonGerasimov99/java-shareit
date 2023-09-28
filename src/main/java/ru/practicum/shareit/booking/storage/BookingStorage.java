package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.StatusEnum;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingStorage extends JpaRepository<Booking, Integer> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Integer bookerId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndIsAfterOrderByStartDesc(Integer bookerId, LocalDateTime start,
                                                                               LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Integer bookerId, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Integer bookerId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Integer bookerId, StatusEnum status, Pageable pageable);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Integer ownerId, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndIsAfterOrderByStartDesc(Integer ownerId, LocalDateTime start,
                                                                                  LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Integer ownerId, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(Integer ownerId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Integer ownerId, StatusEnum status, Pageable pageable);

    // last bookings
    Optional<Booking> findFirstByItemIdAndStartIsBeforeOrderByStartDesc(Integer itemId, LocalDateTime time);

    // next bookings
    Optional<Booking> findFirstByItemIdAndStartIsAfterAndStatusOrderByStartAsc(Integer itemId, LocalDateTime time,
                                                                               StatusEnum status);

}