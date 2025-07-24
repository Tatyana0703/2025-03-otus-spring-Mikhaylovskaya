package ru.otus.hw.dto;

import lombok.Value;
import ru.otus.hw.models.Role;

@Value
public class UserReadDto {

    private long id;

    private String username;

    private Role role;
}
