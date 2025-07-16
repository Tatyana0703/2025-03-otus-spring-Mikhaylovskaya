package ru.otus.hw.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.BookCreateEditDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.GenreRepository;

@Component
@RequiredArgsConstructor
public class BookCreateEditMapper {

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    public Book map(BookCreateEditDto bookDto, Book book) {
        copy(bookDto, book);
        return book;
    }

    public Book map(BookCreateEditDto bookDto) {
        Book book = new Book();
        copy(bookDto, book);
        return book;
    }

    private void copy(BookCreateEditDto bookDto, Book book) {
        Author author = authorRepository.findById(bookDto.getAuthorId())
                .orElseThrow(() -> new NotFoundException("Author with id %d not found"
                        .formatted(bookDto.getAuthorId())));
        Genre genre = genreRepository.findById(bookDto.getGenreId())
                .orElseThrow(() -> new NotFoundException("Genre with id %d not found"
                        .formatted(bookDto.getGenreId())));
        book.setTitle(bookDto.getTitle());
        book.setAuthor(author);
        book.setGenre(genre);
    }
}
