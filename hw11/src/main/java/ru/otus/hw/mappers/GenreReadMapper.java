package ru.otus.hw.mappers;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.GenreReadDto;
import ru.otus.hw.models.Genre;

@Component
public class GenreReadMapper {

    public GenreReadDto map(Genre object) {
        return GenreReadDto.builder()
                .id(object.getId())
                .name(object.getName())
                .build();
    }
}
