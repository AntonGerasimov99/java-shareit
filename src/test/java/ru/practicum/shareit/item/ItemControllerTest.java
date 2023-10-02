package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {

    private final String header = "X-Sharer-User-Id";
    @MockBean
    private ItemService service;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    private ItemDto itemDto;
    private UserDto userDto;
    private User user;

    @BeforeEach
    void beforeEach() {
        userDto = UserDto.builder()
                .id(1)
                .name("name")
                .email("new@mail.ru")
                .build();

        user = User.builder()
                .id(1)
                .name("name")
                .email("new@mail.ru")
                .build();
        itemDto = ItemDto.builder()
                .id(1)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(1)
                .build();
    }

    @Test
    void shouldCreateItem() throws Exception {
        Mockito.when(service.create(itemDto, user.getId())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header(header, 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("itemDescription")))
                .andExpect(jsonPath("$.name", is("itemName")))
                .andExpect(jsonPath("$.available", is(true)));

        verify(service, times(1)).create(itemDto, user.getId());
    }

    @Test
    void shouldGetItemById() throws Exception {
        Mockito.when(service.getItem(itemDto.getId(), user.getId())).thenReturn(itemDto);

        mockMvc.perform(get("/items/{itemId}", itemDto.getId())
                        .header(header, 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("itemDescription")))
                .andExpect(jsonPath("$.name", is("itemName")))
                .andExpect(jsonPath("$.available", is(true)));

        verify(service, times(1)).getItem(itemDto.getId(), user.getId());
    }

    @Test
    void shouldUpdateItemById() throws Exception {
        Mockito.when(service.update(itemDto, user.getId())).thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .header(header, 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("itemDescription")))
                .andExpect(jsonPath("$.name", is("itemName")))
                .andExpect(jsonPath("$.available", is(true)));

        verify(service, times(1)).update(itemDto, user.getId());
    }

    @Test
    void shouldGetAllItemsByUserIdPageable() throws Exception {
        Mockito.when(service.getAllItemsByUserId(user.getId(), 1, 1)).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items")
                        .header(header, 1)
                        .param("from", "1")
                        .param("size", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].description", is("itemDescription")))
                .andExpect(jsonPath("$.[0].name", is("itemName")))
                .andExpect(jsonPath("$.[0].available", is(true)));

        verify(service, times(1)).getAllItemsByUserId(user.getId(), 1, 1);
    }

    @Test
    void shouldGetItemsBySearchPageable() throws Exception {
        Mockito.when(service.search("any", 1, 1)).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "any")
                        .param("from", "1")
                        .param("size", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].description", is("itemDescription")))
                .andExpect(jsonPath("$.[0].name", is("itemName")))
                .andExpect(jsonPath("$.[0].available", is(true)));

        verify(service, times(1)).search("any", 1, 1);
    }

    @Test
    void shouldCreateComment() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .id(1)
                .text("comment")
                .item("1")
                .authorName("user1")
                .build();
        Mockito.when(service.createComment(user.getId(), itemDto.getId(), commentDto)).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header(header, 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is("comment")))
                .andExpect(jsonPath("$.item", is("1")))
                .andExpect(jsonPath("$.authorName", is("user1")));

        verify(service, times(1)).createComment(user.getId(), itemDto.getId(), commentDto);
    }
}