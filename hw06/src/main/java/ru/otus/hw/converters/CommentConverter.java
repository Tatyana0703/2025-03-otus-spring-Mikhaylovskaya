package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Comment;

@Component
@RequiredArgsConstructor
public class CommentConverter {
    private final BookConverter bookConverter;

    public String commentToString(Comment comment) {
        return "Id: %d, Text: %s, book: {%s}"
                .formatted(comment.getId(), comment.getText(), bookConverter.bookAttributesToString(comment.getBook()));
    }
}
