package ru.otus.hw.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BookReadDto {

    private Long id;

    private String title;

    private AuthorReadDto author;

    private GenreReadDto genre;
}
