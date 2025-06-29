package ru.otus.hw.dto;

import lombok.Value;

@Value
public class BookReadDto {

    private long id;

    private String title;

    private AuthorReadDto author;

    private GenreReadDto genre;
}
