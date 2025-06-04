package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.models.Comment;
import java.util.Optional;
import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentExtendRepository {

    @EntityGraph(type = FETCH, attributePaths = {"book"})
    Optional<Comment> findById(long id);
}
