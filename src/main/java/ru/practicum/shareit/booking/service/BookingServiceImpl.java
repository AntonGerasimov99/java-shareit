package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingUtils;
import ru.practicum.shareit.booking.StatusEnum;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundElementException;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingUtils bookingUtils;

    @Override
    @Transactional
    public BookingDto create(Integer userId, BookingDto bookingDto) {
        bookingUtils.validation(userId, bookingDto);
        Booking booking = BookingMapper.toBookingFromDto(bookingDto);
        booking.setStatus(StatusEnum.WAITING);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto get(Integer userId, Integer bookingId) {
        Booking booking = getByBookingId(bookingId);
        bookingUtils.isOwner(userId, booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto updateStatus(Integer userId, Integer bookingId, boolean approved) {
        Booking booking = getByBookingId(bookingId);
        bookingUtils.isApprove(booking, approved);
        if (approved) {
            booking.setStatus(StatusEnum.APPROVED);
        } else {
            booking.setStatus(StatusEnum.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> findAllByBooker(Integer userId, String state) {
        bookingUtils.isUser(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> result = new ArrayList<>();
        switch (state) {
            case "ALL":
                result = bookingRepository.findAllByBookerIdAndStartBeforeAndEndIsAfterOrderByStartDesc(
                        userId, now, now);
                break;
            case "PAST":
                result = bookingRepository.findAllByBookerIdAndEndBeforeStartDesc
                        (userId, now);
                break;
            case "FUTURE":
                result = bookingRepository.findAllByBookerIdAndStartIsAfterDesc
                        (userId, now);
                break;
            case "WAITING":
                result = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc
                        (userId, StatusEnum.WAITING);
                break;
            case "REJECTED":
                result = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc
                        (userId, StatusEnum.REJECTED);
                break;
            default:
                throw new NotFoundElementException();

        }
        return result.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> findAllByOwner(Integer userId, String state) {
        bookingUtils.isUser(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> result = new ArrayList<>();
        switch (state) {
            // проверить ALL на метод
            case "ALL":
                result = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndIsAfterOrderByStartDesc
                        (userId, now, now);
                break;
            case "PAST":
                result = bookingRepository.findAllByItemOwnerIdAndEndBeforeStartDesc
                        (userId, now);
                break;
            case "FUTURE":
                result = bookingRepository.findAllByItemOwnerIdAndStartIsAfterDesc
                        (userId, now);
                break;
            case "WAITING":
                result = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc
                        (userId, StatusEnum.WAITING);
                break;
            case "REJECTED":
                result = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc
                        (userId, StatusEnum.REJECTED);
                break;
            default:
                throw new NotFoundElementException();

        }
        return result.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getByBookingId(Integer bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(NotFoundElementException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getLastBooking(Integer itemId) {
        Booking booking = bookingRepository.findFirstByItemIdAndStartIsBeforeOrderByStartDesc
                (itemId, LocalDateTime.now()).orElseThrow(NotFoundElementException::new);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getNextBooking(Integer itemId) {
        Booking booking = bookingRepository.findFirstByItemIdAndStartIsAfterAndStatusOrderByStartDesc
                (itemId, LocalDateTime.now(), StatusEnum.APPROVED).orElseThrow(NotFoundElementException::new);
        return BookingMapper.toBookingDto(booking);
    }
}
