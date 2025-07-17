package ru.otus.hw.mappers;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.CommentCreateEditDto;
import ru.otus.hw.models.Comment;

@Component
public class CommentCreateEditMapper {

    public Comment map(CommentCreateEditDto commentDto, Comment comment) {
        comment.setText(commentDto.getText());
        comment.setBookId(commentDto.getBookId());
        return comment;
    }

    public Comment map(CommentCreateEditDto commentDto) {
        return new Comment(commentDto.getText(), commentDto.getBookId());
    }
}
