package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import ru.otus.hw.dto.AuthorReadDto;

public interface AuthorService {

    Flux<AuthorReadDto> findAll();
}