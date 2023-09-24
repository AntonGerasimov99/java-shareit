package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Integer userId,
                             @RequestBody BookingDto bookingDto) {
        return bookingService.create(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(@RequestHeader("X-Sharer-User-Id") Integer userId,
                          @PathVariable Integer bookingId) {
        return bookingService.get(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> findAllByBooker(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                            @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findAllByBooker(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllByOwner(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                            @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findAllByOwner(userId, state);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateStatus(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                   @PathVariable Integer bookingId,
                                   @RequestParam Boolean approved) {
        return bookingService.updateStatus(userId, bookingId, approved);
    }
}