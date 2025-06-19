package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.Genre;

@Component
public class GenreConverter {
    public String genreToString(Genre genre) {
        return "Name: %s".formatted(genre.getName());
    }
}