package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Integer userId,
                             @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.create(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(@RequestHeader("X-Sharer-User-Id") Integer userId,
                          @PathVariable Integer bookingId) {
        return bookingService.get(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> findAllByBooker(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                            @RequestParam(defaultValue = "ALL") String state,
                                            @RequestParam(name = "from", defaultValue = "0")
                                            @PositiveOrZero Integer from,
                                            @RequestParam(name = "size", defaultValue = "10")
                                            @Positive Integer size) {
        return bookingService.findAllByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllByOwner(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                           @RequestParam(defaultValue = "ALL") String state,
                                           @RequestParam(name = "from", defaultValue = "0")
                                           @PositiveOrZero Integer from,
                                           @RequestParam(name = "size", defaultValue = "10")
                                           @Positive Integer size) {
        return bookingService.findAllByOwner(userId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateStatus(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                   @PathVariable Integer bookingId,
                                   @RequestParam Boolean approved) {
        return bookingService.updateStatus(userId, bookingId, approved);
    }
}