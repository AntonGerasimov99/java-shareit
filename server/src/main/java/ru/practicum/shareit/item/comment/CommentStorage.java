package ru.practicum.shareit.item.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentStorage extends JpaRepository<Comment, Integer> {

    List<Comment> findAllByItemIdOrderByDate(Integer itemId);
}