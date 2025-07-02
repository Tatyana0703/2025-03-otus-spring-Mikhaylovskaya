package ru.otus.hw.services;

import ru.otus.hw.dto.BookCreateEditDto;
import ru.otus.hw.dto.BookReadDto;
import java.util.List;
import java.util.Optional;

public interface BookService {

    Optional<BookReadDto> findById(long id);

    List<BookReadDto> findAll();

    BookReadDto create(BookCreateEditDto bookDto);

    Optional<BookReadDto> update(long id, BookCreateEditDto bookDto);

    void deleteById(long id);
}