package ru.otus.hw.services;

import ru.otus.hw.dto.UserCreateEditDto;
import ru.otus.hw.dto.UserReadDto;

public interface UserService {

    UserReadDto create(UserCreateEditDto userDto);
}
