package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                         @Valid @RequestBody BookingDto bookingDto) {
        return bookingClient.addBooking(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                      @PathVariable Integer bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByBooker(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                  @RequestParam(defaultValue = "ALL") String state,
                                                  @RequestParam(name = "from", defaultValue = "0")
                                                  @PositiveOrZero Integer from,
                                                  @RequestParam(name = "size", defaultValue = "10")
                                                  @Positive Integer size) {
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllByOwner(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                 @RequestParam(defaultValue = "ALL") String state,
                                                 @RequestParam(name = "from", defaultValue = "0")
                                                 @PositiveOrZero Integer from,
                                                 @RequestParam(name = "size", defaultValue = "10")
                                                 @Positive Integer size) {
        return bookingClient.getBookingsByOwner(userId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateStatus(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                               @PathVariable Integer bookingId,
                                               @RequestParam Boolean approved) {
        return bookingClient.updateStatus(userId, bookingId, approved);
    }
}
