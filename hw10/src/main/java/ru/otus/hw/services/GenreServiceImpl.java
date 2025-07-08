package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.GenreReadDto;
import ru.otus.hw.mappers.GenreReadMapper;
import ru.otus.hw.repositories.GenreRepository;
import java.util.List;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    private final GenreReadMapper genreReadMapper;

    @Override
    public List<GenreReadDto> findAll() {
        return genreRepository.findAll().stream()
                .map(genreReadMapper::map)
                .toList();
    }
}