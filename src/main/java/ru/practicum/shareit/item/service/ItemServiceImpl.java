package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NotFoundElementException;
import ru.practicum.shareit.item.ItemUtils;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final CommentStorage commentStorage;
    private final ItemUtils itemUtils;
    private final BookingService bookingService;

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Integer userId) {
        itemUtils.isUser(userId);
        itemUtils.checkItemValid(itemDto);
        itemDto.setOwner(userId);
        Item item = ItemMapper.toItemFromDTO(itemDto);
        itemStorage.save(item);
        return get(item.getId());
    }

    @Override
    @Transactional
    public ItemDto get(Integer itemId) {
        return ItemMapper.toItemDTO(itemStorage.findById(itemId).
                orElseThrow(NotFoundElementException::new));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItem(Integer itemId, Integer userId) {
        Item item = ItemMapper.toItemFromDTO(get(itemId));
        ItemDto itemDto = get(itemId);
        itemUtils.isUserOwner(userId, itemId);
        updateComments(itemDto);
        return updateBookings(itemDto);
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto itemDto, Integer userId) {
        itemUtils.isItem(itemDto.getId());
        itemUtils.isUser(userId);
        itemUtils.isUserOwner(userId, itemDto.getId());
        itemDto.setOwner(userId);
        Item item = ItemMapper.toItemFromDTO(itemDto);
        return ItemMapper.toItemDTO(itemStorage.save(item));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAllItemsByUserId(Integer userId) {
        List<Item> items = itemStorage.findAllByOwnerId(userId);
        return items.stream()
                .map(ItemMapper::toItemDTO)
                .map(this::updateComments)
                .map(this::updateBookings)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> search(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        List<Item> items = itemStorage.findAllByDescription(text);
        return items.stream()
                .map(ItemMapper::toItemDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(Integer itemId) {
        itemUtils.isItem(itemId);
        itemStorage.deleteById(itemId);
    }

    @Override
    @Transactional
    public CommentDto createComment(Integer userId, Integer itemId, CommentDto commentDto) {
        itemUtils.isBooking(userId,itemId);
        Comment comment = itemUtils.createComment(userId, itemId, commentDto);
        return CommentMapper.toCommentDto(commentStorage.save(comment));
    }

    private ItemDto updateComments(ItemDto itemDto) {
        List<Comment> comments = commentStorage.findAllByItemIdOrderByCreated(itemDto.getId());
        itemDto.setComments(comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));
        return itemDto;
    }

    private ItemDto updateBookings(ItemDto itemDto) {
        Booking lastBooking = BookingMapper.
                toBookingFromDto(bookingService.getLastBooking(itemDto.getId()));
        Booking nextBooking = BookingMapper.
                toBookingFromDto(bookingService.getNextBooking(itemDto.getId()));
        itemDto.setLastBooking(lastBooking != null ? ItemDto.ListBooking.builder()
                .id(lastBooking.getId())
                .bookerId(lastBooking.getBooker())
                .build() :null);
        itemDto.setNextBooking(nextBooking != null ? ItemDto.ListBooking.builder()
                .id(nextBooking.getId())
                .bookerId(nextBooking.getBooker())
                .build() :null);
        return itemDto;
    }
}
