package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Value;
import lombok.experimental.FieldNameConstants;

import java.util.List;

@Value
@FieldNameConstants
public class UserCreateEditDto {

    @NotBlank
    private String username;

    @NotBlank
    private String rawPassword;

    @NotEmpty
    private List<Integer> roleIds;
}