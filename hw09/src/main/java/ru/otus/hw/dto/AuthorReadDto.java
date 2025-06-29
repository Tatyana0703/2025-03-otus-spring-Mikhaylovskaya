package ru.otus.hw.dto;

import lombok.Value;

@Value
public class AuthorReadDto {

    private long id;

    private String fullName;
}
