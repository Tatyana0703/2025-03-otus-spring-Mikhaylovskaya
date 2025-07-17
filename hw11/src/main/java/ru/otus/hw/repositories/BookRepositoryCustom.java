package ru.otus.hw.repositories;

import jakarta.annotation.Nonnull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookReadDto;

public interface BookRepositoryCustom {

    @Nonnull
    Mono<BookReadDto> findByIdCustom(Long id);

    @Nonnull
    Flux<BookReadDto> findAllCustom();
}
