package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import lombok.experimental.FieldNameConstants;

@Value
@FieldNameConstants
public class UserCreateEditDto {

    @NotBlank
    private String username;

    @NotBlank
    private String rawPassword;

    @NotNull
    private Integer roleId;
}