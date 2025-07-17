package ru.otus.hw.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.BookReadDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

@Component
@RequiredArgsConstructor
public class BookReadMapper {

    private final AuthorReadMapper authorReadMapper;

    private final GenreReadMapper genreReadMapper;

    public BookReadDto map(Book book, Author author, Genre genre) {
        return BookReadDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(authorReadMapper.map(author))
                .genre(genreReadMapper.map(genre))
                .build();
    }
}
