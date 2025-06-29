package ru.otus.hw.services;

import ru.otus.hw.dto.GenreReadDto;

import java.util.List;

public interface GenreService {

    List<GenreReadDto> findAll();
}