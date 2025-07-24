package ru.otus.hw.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CommentReadDto {

    private Long id;

    private String text;

    private Long bookId;
}
