package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.StatusEnum;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {

    private final String HEADER = "X-Sharer-User-Id";
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    private BookingDto bookingDto;
    private User firstOwner;
    private User firstBooker;
    private Item firstItem;

    @BeforeEach
    void beforeEach() {
        firstOwner = User.builder().id(1).name("firstOwner").email("firstOwner@mail.ru").build();
        firstBooker = User.builder().id(2).name("firstBooker").email("firstBooker@mail.ru").build();
        firstItem = Item.builder().id(1).name("firstItem").description("firstItem description").available(true)
                .owner(firstOwner).build();
        bookingDto = BookingDto.builder()
                .id(1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1)
                .item(ItemMapper.toItemDTO(firstItem))
                .booker(UserMapper.toUserDTO(firstBooker))
                .status(StatusEnum.WAITING)
                .build();
    }

    @Test
    void shouldCreateBooking() throws Exception {
        Mockito.when(bookingService.create(bookingDto.getId(), bookingDto)).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header(HEADER, 1)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.itemId", is(1)))
                .andExpect(jsonPath("$.item.id", is(1)))
                .andExpect(jsonPath("$.booker.id", is(2)))
                .andExpect(jsonPath("$.status", is(StatusEnum.WAITING.toString())));

        verify(bookingService, times(1)).create(bookingDto.getId(), bookingDto);
    }

    @Test
    void shouldGetBooking() throws Exception {
        Mockito.when(bookingService.get(1, bookingDto.getId())).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header(HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.itemId", is(1)))
                .andExpect(jsonPath("$.item.id", is(1)))
                .andExpect(jsonPath("$.booker.id", is(2)))
                .andExpect(jsonPath("$.status", is(StatusEnum.WAITING.toString())));

        verify(bookingService, times(1)).get(bookingDto.getId(), bookingDto.getId());
    }

    @Test
    void shouldUpdateStatusBooking() throws Exception {
        Mockito.when(bookingService.updateStatus(1, 1, true)).thenReturn(bookingDto);
        bookingDto.setStatus(StatusEnum.APPROVED);

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .header(HEADER, 1)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.itemId", is(1)))
                .andExpect(jsonPath("$.item.id", is(1)))
                .andExpect(jsonPath("$.booker.id", is(2)))
                .andExpect(jsonPath("$.status", is(StatusEnum.APPROVED.toString())));

        verify(bookingService, times(1)).updateStatus(1, 1, true);

        Mockito.when(bookingService.updateStatus(1, 1, false)).thenReturn(bookingDto);
        bookingDto.setStatus(StatusEnum.REJECTED);

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .header(HEADER, 1)
                        .param("approved", "false")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.itemId", is(1)))
                .andExpect(jsonPath("$.item.id", is(1)))
                .andExpect(jsonPath("$.booker.id", is(2)))
                .andExpect(jsonPath("$.status", is(StatusEnum.REJECTED.toString())));

        verify(bookingService, times(1)).updateStatus(1, 1, false);
    }

    @Test
    void shouldFindAllByBooker() throws Exception {
        Mockito.when(bookingService.findAllByBooker(1, "ALL", 1, 1)).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings", 1)
                        .header(HEADER, 1)
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].itemId", is(1)))
                .andExpect(jsonPath("$.[0].item.id", is(1)))
                .andExpect(jsonPath("$.[0].booker.id", is(2)))
                .andExpect(jsonPath("$.[0].status", is(StatusEnum.WAITING.toString())));

        verify(bookingService, times(1)).findAllByBooker(1, "ALL", 1, 1);
    }

    @Test
    void shouldFindAllByOwner() throws Exception {
        Mockito.when(bookingService.findAllByOwner(2, "ALL", 1, 1)).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner", 1)
                        .header(HEADER, 2)
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].itemId", is(1)))
                .andExpect(jsonPath("$.[0].item.id", is(1)))
                .andExpect(jsonPath("$.[0].booker.id", is(2)))
                .andExpect(jsonPath("$.[0].status", is(StatusEnum.WAITING.toString())));

        verify(bookingService, times(1)).findAllByOwner(2, "ALL", 1, 1);
    }
}
