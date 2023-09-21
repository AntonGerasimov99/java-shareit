package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundElementException;
import ru.practicum.shareit.exceptions.ValidationElementException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class ItemUtils {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingService bookingService;

    public Item validateItemForUpdate(Item item) {
        Item oldItem = itemStorage.findById(item.getId()).
                orElseThrow(NotFoundElementException::new);
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
        itemStorage.findById(itemId).
                orElseThrow(NotFoundElementException::new);
    }

    public void isUser(Integer userId) {
        userStorage.findById(userId).
                orElseThrow(NotFoundElementException::new);
    }

    public void isUserOwner(Integer userId, Integer itemId) {
        if (!Objects.equals(itemStorage.getById(itemId).getOwnerId(), userId)) {
            throw new NotFoundElementException();
        }
    }

    public void isBooking(Integer userId, Integer itemId) {
        List<BookingDto> bookings = bookingService.findAllByBooker(userId, "ALL");
        bookings.stream()
                .filter(bookingDto -> bookingDto.getItemId()==itemId)
                .filter(bookingDto -> bookingDto.getStatus().equals("APPROVED"));
        if (bookings.isEmpty()) {
            throw new NotFoundElementException();
        }
    }

    public Comment createComment(Integer userId, Integer itemId, CommentDto commentDto) {
        Item item = itemStorage.findById(itemId).
                orElseThrow(NotFoundElementException::new);
        User author = userStorage.findById(userId).
                orElseThrow(NotFoundElementException::new);
        Comment comment = CommentMapper.toCommentFromDTO(commentDto);
        comment.setItem(item);
        comment.setAuthor(author);
        return comment;
    }
}
