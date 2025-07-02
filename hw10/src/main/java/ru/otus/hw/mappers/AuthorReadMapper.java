package ru.otus.hw.mappers;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.AuthorReadDto;
import ru.otus.hw.models.Author;

@Component
public class AuthorReadMapper {

    public AuthorReadDto map(Author object) {
        return new AuthorReadDto(
                object.getId(),
                object.getFullName()
        );
    }
}
