package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.comment.CommentDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class ItemDto {

    private Integer id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private Integer owner;
    private Integer request;
    private ListBooking lastBooking;
    private ListBooking nextBooking;
    private List<CommentDto> comments;


    @Data
    @Builder
    public static class ListBooking {
        private Integer id;
        private Integer bookerId;
    }
}
