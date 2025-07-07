package ru.otus.hw.dto;

import lombok.Value;

@Value
public class CommentReadDto {

    private long id;

    private String text;

    private long bookId;
}
