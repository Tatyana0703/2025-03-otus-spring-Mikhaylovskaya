package ru.otus.hw.services;

import org.springframework.security.core.userdetails.UserDetails;
import ru.otus.hw.dto.CommentCreateEditDto;
import ru.otus.hw.dto.CommentReadDto;
import java.util.List;
import java.util.Optional;

public interface CommentService {

    Optional<CommentReadDto> findById(long id);

    List<CommentReadDto> findAllByBookId(long bookId);

    CommentReadDto create(CommentCreateEditDto commentDto, UserDetails userDetails);

    CommentReadDto update(long id, CommentCreateEditDto commentDto, UserDetails userDetails);

    void deleteById(long id);

    boolean checkCommentOwner(long id, UserDetails userDetails);
}
