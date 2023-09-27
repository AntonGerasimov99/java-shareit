package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.utils.BookingUtils;
import ru.practicum.shareit.booking.model.StatusEnum;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exceptions.NotFoundElementException;
import ru.practicum.shareit.exceptions.UnknownStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingStorage bookingRepository;
    private final BookingUtils bookingUtils;

    @Override
    @Transactional
    public BookingDto create(Integer userId, BookingDto bookingDto) {
        Booking booking = bookingUtils.validation(userId, bookingDto);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto get(Integer userId, Integer bookingId) {
        Booking booking = getByBookingId(bookingId);
        bookingUtils.isOwnerOrBooker(userId, booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto updateStatus(Integer userId, Integer bookingId, boolean approved) {
        Booking booking = getByBookingId(bookingId);
        bookingUtils.isOwner(userId, booking);
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
        State stateEnum = State.checkState(state);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> result;
        switch (stateEnum) {
            case ALL:
                result = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                result = bookingRepository.findAllByBookerIdAndStartBeforeAndEndIsAfterOrderByStartDesc(userId, now, now);
                break;
            case PAST:
                result = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
                break;
            case FUTURE:
                result = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, now);
                break;
            case WAITING:
                result = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, StatusEnum.WAITING);
                break;
            case REJECTED:
                result = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, StatusEnum.REJECTED);
                break;
            default:
                throw new UnknownStatusException("Unknown state: " + state);

        }
        return result.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> findAllByOwner(Integer userId, String state) {
        bookingUtils.isUser(userId);
        State stateEnum = State.checkState(state);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> result;
        switch (stateEnum) {
            case ALL:
                result = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                result = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndIsAfterOrderByStartDesc(userId, now, now);
                break;
            case PAST:
                result = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, now);
                break;
            case FUTURE:
                result = bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, now);
                break;
            case WAITING:
                result = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, StatusEnum.WAITING);
                break;
            case REJECTED:
                result = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, StatusEnum.REJECTED);
                break;
            default:
                throw new UnknownStatusException("Unknown state: " + state);

        }
        return result.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getByBookingId(Integer bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundElementException("Букинг не найден"));
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getLastBooking(Integer itemId) {
        return bookingRepository.findFirstByItemIdAndStartIsBeforeOrderByStartDesc(itemId, LocalDateTime.now())
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getNextBooking(Integer itemId) {
        return bookingRepository.findFirstByItemIdAndStartIsAfterAndStatusOrderByStartAsc(itemId, LocalDateTime.now(),
                StatusEnum.APPROVED).orElse(null);
    }
}