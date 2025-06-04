package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.models.Book;
import java.util.List;
import java.util.Optional;
import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

public interface BookRepository extends JpaRepository<Book, Long> {

    @EntityGraph(type = FETCH, attributePaths = {"author", "genre"})
    Optional<Book> findById(long id);

    @EntityGraph(type = FETCH, attributePaths = {"author", "genre"})
    List<Book> findAll();
}