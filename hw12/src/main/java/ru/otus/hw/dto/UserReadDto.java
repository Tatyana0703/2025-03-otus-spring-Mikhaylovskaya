package ru.otus.hw.dto;

import lombok.Value;

@Value
public class UserReadDto {

    private long id;

    private String username;

    private RoleReadDto role;
}
