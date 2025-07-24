package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookCreateEditDto;
import ru.otus.hw.dto.BookReadDto;

public interface BookService {

    Mono<BookReadDto> findById(Long id);

    Flux<BookReadDto> findAll();

    Mono<BookReadDto> create(BookCreateEditDto bookDto);

    Mono<BookReadDto> update(Long id, BookCreateEditDto bookDto);

    Mono<Void> deleteById(Long id);
}