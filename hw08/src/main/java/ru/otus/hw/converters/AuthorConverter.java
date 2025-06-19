package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.Author;

@Component
public class AuthorConverter {
    public String authorToString(Author author) {
        return "FullName: %s".formatted(author.getFullName());
    }
}