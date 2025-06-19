package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.Book;
import java.util.List;
import ru.otus.hw.dto.Author;
import ru.otus.hw.dto.Genre;

public interface BookRepository extends MongoRepository<Book, String>, BookCustomRepository {

    @Aggregation("{ $group: { _id : $author} }")
    List<Author> findAuthors();

    @Aggregation("{ $group: { _id : $genre} }")
    List<Genre> findGenres();
}