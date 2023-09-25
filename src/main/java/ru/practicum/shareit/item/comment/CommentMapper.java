package ru.practicum.shareit.item.comment;

public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .item(comment.getItem().getName())
                .authorName(comment.getAuthor().getName())
                .created(comment.getDate())
                .build();
    }

    public static Comment toCommentFromDTO(CommentDto commentDto) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .date(commentDto.getCreated())
                .build();
    }
}