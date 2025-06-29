package ru.otus.hw.services;

import ru.otus.hw.dto.CommentCreateEditDto;
import ru.otus.hw.dto.CommentReadDto;
import java.util.List;
import java.util.Optional;

public interface CommentService {

    Optional<CommentReadDto> findById(long id);

    List<CommentReadDto> findByBookId(long bookId);

    CommentReadDto create(CommentCreateEditDto commentDto);

    Optional<CommentReadDto> update(long id, CommentCreateEditDto commentDto);

    void deleteById(long id);
}
