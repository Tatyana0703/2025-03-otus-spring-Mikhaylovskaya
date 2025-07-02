package ru.otus.hw.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.AuthorReadDto;
import ru.otus.hw.dto.BookReadDto;
import ru.otus.hw.dto.GenreReadDto;
import ru.otus.hw.models.Book;

@Component
@RequiredArgsConstructor
public class BookReadMapper {

    private final AuthorReadMapper authorReadMapper;

    private final GenreReadMapper genreReadMapper;

    public BookReadDto map(Book book) {
        AuthorReadDto author = authorReadMapper.map(book.getAuthor());
        GenreReadDto genre = genreReadMapper.map(book.getGenre());
        return new BookReadDto(
                book.getId(),
                book.getTitle(),
                author,
                genre
        );
    }
}