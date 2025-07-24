package ru.otus.hw.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GenreReadDto {

    private Long id;

    private String name;
}
