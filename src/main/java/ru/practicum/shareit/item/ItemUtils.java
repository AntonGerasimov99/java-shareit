package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.StatusEnum;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NotFoundElementException;
import ru.practicum.shareit.exceptions.ValidationElementException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class ItemUtils {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingService bookingService;

    public Item validateItemForUpdate(Item item) {
        Item oldItem = itemStorage.findById(item.getId())
                        .orElseThrow(() -> new NotFoundElementException("Предмет не найден"));
        if (item.getName() == null || item.getName().isBlank()) {
            item.setName(oldItem.getName());
        }
        if (item.getDescription() == null || item.getDescription().isEmpty()) {
            item.setDescription(oldItem.getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(oldItem.getAvailable());
        }
        return item;
    }

    public void checkItemValid(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationElementException("Имя отсутствует");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationElementException("Описание отсутствует");
        }
    }

    public void isItem(Integer itemId) {
        itemStorage.findById(itemId)
                        .orElseThrow(() -> new NotFoundElementException("Предмет не найден"));
    }

    public void isUser(Integer userId) {
        userStorage.findById(userId)
                        .orElseThrow(() -> new NotFoundElementException("Пользователь не найден"));
    }

    public void isUserOwner(Integer userId, Integer itemId) {
        if (!Objects.equals(itemStorage.getById(itemId).getOwner().getId(), userId)) {
            throw new NotFoundElementException("Пользователь не является владельцем");
        }
    }

    public void isBooking(Integer userId, Integer itemId) {
        List<BookingDto> bookings = bookingService.findAllByBooker(userId, "ALL");
        if (bookings.stream()
                .filter(bookingDto -> Objects.equals(bookingDto.getItem().getId(), itemId))
                .filter(bookingDto -> bookingDto.getStatus().equals(StatusEnum.APPROVED))
                .filter(bookingDto -> bookingDto.getStart().isBefore(LocalDateTime.now()))
                .count() == 0) {
            throw new ValidationElementException("Список букингов пустой");
        }
    }

    public Comment createComment(Integer userId, Integer itemId, CommentDto commentDto) {
        if (commentDto.getText().isEmpty()) {
            throw new ValidationElementException("Текст комментария пуст");
        }
        Item item = itemStorage.findById(itemId).
                orElseThrow(() -> new NotFoundElementException("Предмет не найден"));
        User author = userStorage.findById(userId).
                orElseThrow(() -> new NotFoundElementException("Пользователь не найден"));
        Comment comment = CommentMapper.toCommentFromDTO(commentDto);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setDate(LocalDateTime.now());
        return comment;
    }
}