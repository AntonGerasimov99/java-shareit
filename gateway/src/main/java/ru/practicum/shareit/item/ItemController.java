package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @Valid @RequestBody ItemDto itemDto) {
        return itemClient.addItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                              @PathVariable("itemId") Integer itemId) {
        return itemClient.getItemById(itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable("itemId") Integer itemId,
                                             @RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @RequestBody ItemDto itemDto) {
        itemDto.setId(itemId);
        return itemClient.updateItem(userId, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                      @RequestParam(name = "from", defaultValue = "0")
                                                      @PositiveOrZero Integer from,
                                                      @RequestParam(name = "size", defaultValue = "10")
                                                      @Positive Integer size) {
        return itemClient.getItemsByUserIdWithPagination(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text,
                                         @RequestParam(name = "from", defaultValue = "0")
                                         @PositiveOrZero Integer from,
                                         @RequestParam(name = "size", defaultValue = "10")
                                         @Positive Integer size) {
        return itemClient.searchPagination(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                @PathVariable(value = "itemId") Integer itemId,
                                                @RequestBody CommentDto commentDto) {
        return itemClient.addComment(userId, itemId, commentDto);
    }
}