package ru.otus.hw.mappers;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.AuthorReadDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.projections.AuthorProjection;

@Component
public class AuthorReadMapper {

    public AuthorReadDto map(Author object) {
        return AuthorReadDto.builder()
                .id(object.getId())
                .fullName(object.getFullName())
                .build();
    }

    public AuthorReadDto map(AuthorProjection object) {
        return AuthorReadDto.builder()
                .id(object.getId())
                .fullName(object.getFullName())
                .build();
    }
}
