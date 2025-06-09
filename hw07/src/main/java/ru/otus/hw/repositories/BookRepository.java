package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.otus.hw.models.Book;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("select b from Book b left join fetch b.author left join fetch b.genre where b.id = :id")
    Optional<Book> findBookWithAuthorAndGenre(long id);

    @Query("select b from Book b left join fetch b.author left join fetch b.genre")
    List<Book> findAllBooksWithAuthorAndGenre();
}