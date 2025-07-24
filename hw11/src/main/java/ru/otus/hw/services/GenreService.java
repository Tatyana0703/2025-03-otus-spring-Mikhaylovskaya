package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import ru.otus.hw.dto.GenreReadDto;

public interface GenreService {

    Flux<GenreReadDto> findAll();
}