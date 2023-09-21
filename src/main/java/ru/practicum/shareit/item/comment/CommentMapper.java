package ru.practicum.shareit.item.comment;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Component
public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .item(comment.getItem().getName())
                .author(comment.getAuthor().getName())
                .date(comment.getDate())
                .build();
    }

    public static Comment toCommentFromDTO(CommentDto commentDto) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .date(commentDto.getDate())
                .build();
    }
}
