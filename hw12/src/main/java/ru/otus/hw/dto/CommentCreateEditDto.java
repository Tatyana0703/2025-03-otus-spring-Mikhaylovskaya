package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class CommentCreateEditDto {

    @NotBlank
    private String text;

    @NotNull
    private Long bookId;
}
