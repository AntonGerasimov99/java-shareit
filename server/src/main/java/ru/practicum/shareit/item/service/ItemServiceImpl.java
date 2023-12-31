package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NotFoundElementException;
import ru.practicum.shareit.item.utils.ItemUtils;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final CommentStorage commentStorage;
    private final ItemUtils itemUtils;
    private final BookingService bookingService;
    private final ItemRequestStorage itemRequestStorage;

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Integer userId) {
        itemUtils.isUser(userId);
        itemUtils.checkItemValid(itemDto);
        itemDto.setOwner(userId);
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundElementException("Пользователь не найден"));
        Item item = ItemMapper.toItemFromDTO(itemDto, user);
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestStorage.findById(itemDto.getRequestId()).orElseThrow();
            item.setRequest(itemRequest);
            itemStorage.save(item);
            return get(item.getId());
        }
        itemStorage.save(item);
        return get(item.getId());
    }

    @Override
    @Transactional
    public ItemDto get(Integer itemId) {
        return ItemMapper.toItemDTO(itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundElementException("Предмет не найден")));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItem(Integer itemId, Integer userId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundElementException("Пользователь не найден"));
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundElementException("Предмет не найден"));
        ItemDto itemDto = ItemMapper.toItemDTO(item);
        if (item.getOwner().getId().equals(userId)) {
            updateBookings(itemDto);
        }
        updateComments(itemDto);
        return itemDto;
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto itemDto, Integer userId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundElementException("Пользователь не найден"));
        Item oldItem = itemStorage.findById(itemDto.getId())
                .orElseThrow(() -> new NotFoundElementException("Предмет не найден"));
        itemUtils.isUserOwner(userId, itemDto.getId());
        itemDto.setOwner(userId);

        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            itemDto.setName(oldItem.getName());
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            itemDto.setDescription(oldItem.getDescription());
        }
        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(oldItem.getAvailable());
        }
        if (itemDto.getOwner() == null) {
            itemDto.setOwner(oldItem.getOwner().getId());
        }
        Item item = ItemMapper.toItemFromDTO(itemDto, user);
        return ItemMapper.toItemDTO(itemStorage.save(item));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAllItemsByUserId(Integer userId, Integer from, Integer size) {
        List<Item> items = itemStorage.findAllByOwnerId(userId, PageRequest.of(from / size, size));
        return items.stream()
                .map(ItemMapper::toItemDTO)
                .map(this::updateComments)
                .map(this::updateBookings)
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> search(String text, Integer from, Integer size) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        List<Item> items = itemStorage.findAllByDescriptionContainsIgnoreCase(text, PageRequest.of(from / size, size));
        return items.stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto createComment(Integer userId, Integer itemId, CommentDto commentDto) {
        itemUtils.isBooking(userId, itemId);
        Comment comment = itemUtils.createComment(userId, itemId, commentDto);
        return CommentMapper.toCommentDto(commentStorage.save(comment));
    }

    public ItemDto updateComments(ItemDto itemDto) {
        List<Comment> comments = commentStorage.findAllByItemIdOrderByDate(itemDto.getId());
        itemDto.setComments(comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));
        return itemDto;
    }

    public ItemDto updateBookings(ItemDto itemDto) {
        Optional<Booking> lastBooking = Optional.ofNullable(bookingService.getLastBooking(itemDto.getId()));
        Optional<Booking> nextBooking = Optional.ofNullable(bookingService.getNextBooking(itemDto.getId()));
        lastBooking.ifPresent(booking -> itemDto.setLastBooking(ItemDto.ListBooking.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build()));
        nextBooking.ifPresent(booking -> itemDto.setNextBooking(ItemDto.ListBooking.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build()));
        return itemDto;
    }
}