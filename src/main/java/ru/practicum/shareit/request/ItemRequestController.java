package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                        @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.create(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestService.findAllByUserId(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findByRequestId(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                          @PathVariable("requestId") Integer requestId) {
        return itemRequestService.findByUserIdAndRequestId(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAllPageableByUserId(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                        @RequestParam(name = "from", defaultValue = "0")
                                                        @PositiveOrZero Integer from,
                                                        @RequestParam(name = "size", defaultValue = "10")
                                                        @Positive Integer size) {
        return itemRequestService.findAllPageableByUserId(userId, from, size);
    }
}
