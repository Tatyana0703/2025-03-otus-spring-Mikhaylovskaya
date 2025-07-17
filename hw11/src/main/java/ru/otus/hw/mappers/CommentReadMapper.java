package ru.otus.hw.mappers;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.CommentReadDto;
import ru.otus.hw.models.Comment;

@Component
public class CommentReadMapper {

    public CommentReadDto map(Comment object) {
        return CommentReadDto.builder()
                .id(object.getId())
                .text(object.getText())
                .bookId(object.getBookId())
                .build();
    }
}
