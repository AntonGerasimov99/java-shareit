package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                              @Valid @RequestBody ItemDto itemDto) {
        return itemService.create(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                               @PathVariable("itemId") Integer itemId) {
        return itemService.getItem(itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable("itemId") Integer itemId,
                              @RequestHeader("X-Sharer-User-Id") Integer userId,
                              @RequestBody ItemDto itemDto) {
        itemDto.setId(itemId);
        return itemService.update(itemDto, userId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getAllItemsByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }

    @DeleteMapping
    public void deleteItem(@PathVariable("itemId") Integer itemId) {
        itemService.deleteItem(itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                    @PathVariable(value = "itemId") Integer itemId,
                                    @RequestBody CommentDto commentDto) {
        return itemService.createComment(userId, itemId, commentDto);
    }
}
