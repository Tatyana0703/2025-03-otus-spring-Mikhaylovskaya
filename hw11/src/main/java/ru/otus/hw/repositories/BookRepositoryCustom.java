package ru.otus.hw.repositories;

import jakarta.annotation.Nonnull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.projections.BookProjection;

public interface BookRepositoryCustom {

    @Nonnull
    Mono<BookProjection> findByIdCustom(Long id);

    @Nonnull
    Flux<BookProjection> findAllCustom();
}
