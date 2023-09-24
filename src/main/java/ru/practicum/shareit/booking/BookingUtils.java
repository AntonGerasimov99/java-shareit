package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.NotFoundElementException;
import ru.practicum.shareit.exceptions.ValidationElementException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
@AllArgsConstructor
public class BookingUtils {

    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    public Booking validation(Integer userId, BookingDto bookingDto) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundElementException("Пользователь не найден"));
        Item item = itemStorage.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundElementException("Предмет не найден"));
        if (Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundElementException("Владелец не может забронировать вещь");
        }
        if (!item.getAvailable()) {
            throw new ValidationElementException("Вещь недоступна");
        }

        Booking booking = BookingMapper.toBookingFromDto(bookingDto);
        booking.setStatus(StatusEnum.WAITING);
        booking.setItem(item);
        booking.setBooker(user);
        validationDate(booking);
        return booking;
    }

    public void validationDate(Booking booking) {
        LocalDateTime start = booking.getStart();
        LocalDateTime end = booking.getEnd();

        if (start == null || end == null) {
            throw new ValidationElementException("Не указана дата аренды");
        }

        if (start.isEqual(end)) {
            throw new ValidationElementException("Начало и конец аренды равны");
        }

        if (start.isBefore(LocalDateTime.now()) || end.isBefore(LocalDateTime.now())) {
            throw new ValidationElementException("Начало/конец аренды указан в прошлом");
        }
        if (end.isBefore(start)) {
            throw new ValidationElementException("Начало позже конца аренды");
        }
    }

    public void isOwnerOrBooker(Integer userId, Booking booking) {
        if (!Objects.equals(itemStorage.getById(booking.getItem().getId()).getOwner().getId(), userId)
                && !Objects.equals(booking.getBooker().getId(), userId)) {
            throw new NotFoundElementException("Пользователь не является владельцем вещи или букером");
        }
    }

    public void isOwner(Integer userId, Booking booking) {
        if (!Objects.equals(itemStorage.getById(booking.getItem().getId()).getOwner().getId(), userId)) {
            throw new NotFoundElementException("Пользователь не является владельцем вещи");
        }
    }

    public void isUser(Integer userId) {
        User user = userStorage.findById(userId).
                orElseThrow(() -> new NotFoundElementException("Пользователь не найден"));
    }

    public void isApprove(Booking booking, boolean approved) {
        if ((approved && booking.getStatus().equals(StatusEnum.APPROVED))
                || (approved && booking.getStatus().equals(StatusEnum.REJECTED))) {
            throw new ValidationElementException("У букинга уже выставлен данный статус");
        }
    }
}