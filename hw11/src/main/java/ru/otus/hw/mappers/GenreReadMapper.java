package ru.otus.hw.mappers;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.GenreReadDto;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.projections.GenreProjection;

@Component
public class GenreReadMapper {

    public GenreReadDto map(Genre object) {
        return GenreReadDto.builder()
                .id(object.getId())
                .name(object.getName())
                .build();
    }

    public GenreReadDto map(GenreProjection object) {
        return GenreReadDto.builder()
                .id(object.getId())
                .name(object.getName())
                .build();
    }
}
