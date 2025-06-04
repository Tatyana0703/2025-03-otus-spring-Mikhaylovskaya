package ru.otus.hw.repositories;

import ru.otus.hw.models.Comment;
import java.util.List;

public interface CommentExtendRepository {
    List<Comment> findByBookId(long bookId);
}
