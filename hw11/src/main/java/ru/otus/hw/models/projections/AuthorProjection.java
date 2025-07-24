package ru.otus.hw.models.projections;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthorProjection {

    private Long id;

    private String fullName;
}
