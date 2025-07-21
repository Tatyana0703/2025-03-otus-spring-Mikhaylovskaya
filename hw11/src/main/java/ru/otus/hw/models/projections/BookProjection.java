package ru.otus.hw.models.projections;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BookProjection {

    private Long id;

    private String title;

    private AuthorProjection author;

    private GenreProjection genre;
}
