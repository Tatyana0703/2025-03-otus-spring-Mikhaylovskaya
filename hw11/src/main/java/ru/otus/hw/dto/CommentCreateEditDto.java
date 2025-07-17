package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CommentCreateEditDto {

    @NotBlank
    private String text;

    @NotNull
    private Long bookId;
}
