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

    public void validation(Integer userId, BookingDto bookingDto) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundElementException("Пользователь не найден"));
        Item item = itemStorage.findById(bookingDto.getId())
                .orElseThrow(() -> new NotFoundElementException("Предмет не найден"));
        if (!item.getAvailable()) {
            throw new ValidationElementException("Вещь недоступна");
        }
        validationDate(bookingDto);
    }

    public void validationDate(BookingDto bookingDto) {
        Booking booking = BookingMapper.toBookingFromDto(bookingDto);
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
    }

    // NotFoundElementException???
    public void isOwner(Integer userId, Booking booking) {
        if (!Objects.equals(itemStorage.getById(booking.getItem().getId()).getOwner(), userId)) {
            throw new ValidationElementException("Пользователь не является владельцем вещи");
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