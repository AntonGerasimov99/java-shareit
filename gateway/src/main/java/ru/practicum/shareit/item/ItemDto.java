package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;

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
    private Integer requestId;
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