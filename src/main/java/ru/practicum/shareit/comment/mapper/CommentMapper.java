package ru.practicum.shareit.comment.mapper;

import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.model.CommentInputDto;
import ru.practicum.shareit.comment.model.CommentResponseDto;

import java.time.LocalDateTime;

public class CommentMapper {
    public static Comment toComment(CommentInputDto commentInputDto) {
        return new Comment(commentInputDto.getText(), LocalDateTime.now());
    }

    public static CommentResponseDto toCommentRespDto(Comment comment) {
        return new CommentResponseDto(comment.getId(), comment.getText(), comment.getItem().getId(), comment.getAuthor().getName(), comment.getCreated());
    }

    public static CommentInputDto toCommentInputDto(Comment comment) {
        return new CommentInputDto(comment.getText());
    }
}
