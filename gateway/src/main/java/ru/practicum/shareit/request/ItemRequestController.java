package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.addRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestClient.getUserRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findByRequestId(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                  @PathVariable("requestId") Integer requestId) {
        return itemRequestClient.getRequest(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllPageableByUserId(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                          @RequestParam(name = "from", defaultValue = "0")
                                                          @PositiveOrZero Integer from,
                                                          @RequestParam(name = "size", defaultValue = "10")
                                                          @Positive Integer size) {
        return itemRequestClient.getAllRequests(userId, from, size);
    }
}
