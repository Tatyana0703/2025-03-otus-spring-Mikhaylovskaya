package ru.otus.hw.dto;

import lombok.Value;
import java.util.Set;

@Value
public class UserReadDto {

    private long id;

    private String username;

    private Set<RoleReadDto> roles;
}
