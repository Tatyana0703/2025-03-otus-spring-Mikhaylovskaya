package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.otus.hw.dto.GenreReadDto;
import ru.otus.hw.mappers.GenreReadMapper;
import ru.otus.hw.repositories.GenreRepository;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    private final GenreReadMapper genreReadMapper;

    @Override
    public Flux<GenreReadDto> findAll() {
        return genreRepository.findAll().map(genreReadMapper::map);
    }
}