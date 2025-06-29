package ru.otus.hw.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.CommentCreateEditDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;

@Component
@RequiredArgsConstructor
public class CommentCreateEditMapper {

    private final BookRepository bookRepository;

    public Comment map(CommentCreateEditDto commentDto, Comment comment) {
        copy(commentDto, comment);
        return comment;
    }

    public Comment map(CommentCreateEditDto commentDto) {
        Comment comment = new Comment();
        copy(commentDto, comment);
        return comment;
    }

    private void copy(CommentCreateEditDto commentDto, Comment comment) {
        Book book = bookRepository.findById(commentDto.getBookId())
                .orElseThrow(() -> new NotFoundException("Book with id %d not found"
                        .formatted(commentDto.getBookId())));
        comment.setText(commentDto.getText());
        comment.setBook(book);
    }
}
