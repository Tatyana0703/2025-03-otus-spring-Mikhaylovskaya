package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentCreateEditDto;
import ru.otus.hw.dto.CommentReadDto;

public interface CommentService {

    Mono<CommentReadDto> findById(Long id);

    Flux<CommentReadDto> findByBookId(Long bookId);

    Mono<CommentReadDto> create(CommentCreateEditDto commentDto);

    Mono<CommentReadDto> update(Long id, CommentCreateEditDto commentDto);

    Mono<Void> deleteById(Long id);
}
