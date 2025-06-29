package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import lombok.experimental.FieldNameConstants;

@Value
@FieldNameConstants
public class BookCreateEditDto {

    @NotBlank
    private String title;

    @NotNull
    private Long authorId;

    @NotNull
    private Long genreId;
}
