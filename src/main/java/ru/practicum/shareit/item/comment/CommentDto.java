package ru.practicum.shareit.item.comment;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {

    private Integer id;
    private String text;
    private String item;
    private String authorName;
    private LocalDateTime created;
}