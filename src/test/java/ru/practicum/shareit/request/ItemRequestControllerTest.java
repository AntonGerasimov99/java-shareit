package ru.practicum.shareit.request;

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
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {

    private final String HTTP_HEADER_USER_ID = "X-Sharer-User-Id";
    @MockBean
    private ItemRequestService service;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    private Item item;
    private User user;
    private User user2;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private LocalDateTime createdDate;

    @BeforeEach
    void beforeEach() {
        createdDate = LocalDateTime.now();
        user = User.builder()
                .id(1)
                .name("UserName")
                .email("new@mail.ru")
                .build();

        user2 = User.builder()
                .id(2)
                .name("UserName2")
                .email("new2@mail.ru")
                .build();

        item = Item.builder()
                .id(1)
                .name("name")
                .description("description")
                .available(true)
                .owner(user2)
                .build();

        itemRequest = ItemRequest.builder()
                .id(1)
                .description("description")
                .requester(user)
                .created(createdDate)
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(1)
                .description("description")
                .items(List.of(ItemMapper.toItemDTO(item)))
                .build();
    }

    @Test
    void shouldCreateRequest() throws Exception {
        Mockito.when(service.create(itemRequestDto, user.getId())).thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header(HTTP_HEADER_USER_ID, 1)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.items.[0].id", is(1)))
                .andExpect(jsonPath("$.items.[0].name", is("name")))
                .andExpect(jsonPath("$.items.[0].available", is(true)));

        verify(service, times(1)).create(itemRequestDto, user.getId());
    }

    @Test
    void shouldFindAllByUserId() throws Exception {
        Mockito.when(service.findAllByUserId(user.getId())).thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests")
                        .header(HTTP_HEADER_USER_ID, 1)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].description", is("description")))
                .andExpect(jsonPath("$.[0].items.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].items.[0].name", is("name")))
                .andExpect(jsonPath("$.[0].items.[0].available", is(true)));

        verify(service, times(1)).findAllByUserId(user.getId());
    }

    @Test
    void shouldFindAllByRequestId() throws Exception {
        Mockito.when(service.findByUserIdAndRequestId(user.getId(), itemRequestDto.getId())).thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/{requestId}", 1)
                        .header(HTTP_HEADER_USER_ID, 1)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.items.[0].id", is(1)))
                .andExpect(jsonPath("$.items.[0].name", is("name")))
                .andExpect(jsonPath("$.items.[0].available", is(true)));

        verify(service, times(1)).findByUserIdAndRequestId(user.getId(), itemRequestDto.getId());
    }

    @Test
    void shouldFindAllByRequestIdPageable() throws Exception {
        Mockito.when(service.findAllPageableByUserId(user.getId(), 1, 1)).thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests/all")
                        .header(HTTP_HEADER_USER_ID, 1)
                        .param("from", "1")
                        .param("size", "1")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].description", is("description")))
                .andExpect(jsonPath("$.[0].items.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].items.[0].name", is("name")))
                .andExpect(jsonPath("$.[0].items.[0].available", is(true)));

        verify(service, times(1)).findAllPageableByUserId(user.getId(), 1, 1);
    }
}
