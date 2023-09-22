package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    BookingDto create(Integer userId, BookingDto bookingDto);

    BookingDto get(Integer userId, Integer bookingId);

    Booking getByBookingId(Integer bookingId);

    BookingDto updateStatus(Integer userId, Integer bookingId, boolean approved);

    List<BookingDto> findAllByBooker(Integer userId, String state);

    List<BookingDto> findAllByOwner(Integer userId, String state);
    Booking getLastBooking(Integer itemId);
    Booking getNextBooking(Integer itemId);
}
