package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public List<BookingDto> findAllByBooker(Integer userId, String state, Integer from, Integer size) {
        bookingUtils.isUser(userId);
        State stateEnum = State.checkState(state);
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from / size, size);
        List<Booking> result;
        switch (stateEnum) {
            case ALL:
                result = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageable);
                break;
            case CURRENT:
                result = bookingRepository.findAllByBookerIdAndStartBeforeAndEndIsAfterOrderByStartDesc(userId, now, now, pageable);
                break;
            case PAST:
                result = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now, pageable);
                break;
            case FUTURE:
                result = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, now, pageable);
                break;
            case WAITING:
                result = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, StatusEnum.WAITING, pageable);
                break;
            case REJECTED:
                result = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, StatusEnum.REJECTED, pageable);
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
    public List<BookingDto> findAllByOwner(Integer userId, String state, Integer from, Integer size) {
        bookingUtils.isUser(userId);
        State stateEnum = State.checkState(state);
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from / size, size);
        List<Booking> result;
        switch (stateEnum) {
            case ALL:
                result = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId, pageable);
                break;
            case CURRENT:
                result = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndIsAfterOrderByStartDesc(userId, now, now, pageable);
                break;
            case PAST:
                result = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, now, pageable);
                break;
            case FUTURE:
                result = bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, now, pageable);
                break;
            case WAITING:
                result = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, StatusEnum.WAITING, pageable);
                break;
            case REJECTED:
                result = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, StatusEnum.REJECTED, pageable);
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