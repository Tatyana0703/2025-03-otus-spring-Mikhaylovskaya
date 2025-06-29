package ru.otus.hw.services;

import ru.otus.hw.dto.AuthorReadDto;

import java.util.List;

public interface AuthorService {

    List<AuthorReadDto> findAll();

}