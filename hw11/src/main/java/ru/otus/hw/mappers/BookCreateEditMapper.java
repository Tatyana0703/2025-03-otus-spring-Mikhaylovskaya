package ru.otus.hw.mappers;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.BookCreateEditDto;
import ru.otus.hw.models.Book;

@Component
public class BookCreateEditMapper {

    public Book map(BookCreateEditDto bookDto, Book book) {
        book.setTitle(bookDto.getTitle());
        return book;
    }

    public Book map(BookCreateEditDto bookDto) {
        return new Book(bookDto.getTitle(), bookDto.getAuthorId(), bookDto.getGenreId());
    }
}
