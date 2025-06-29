package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.AuthorReadDto;
import ru.otus.hw.mappers.AuthorReadMapper;
import ru.otus.hw.repositories.AuthorRepository;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    private final AuthorReadMapper authorReadMapper;

    @Override
    public List<AuthorReadDto> findAll() {
        return authorRepository.findAll().stream()
                .map(authorReadMapper::map)
                .toList();
    }
}