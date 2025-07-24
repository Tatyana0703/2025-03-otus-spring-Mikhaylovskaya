package ru.otus.hw.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthorReadDto {

    private Long id;

    private String fullName;
}
