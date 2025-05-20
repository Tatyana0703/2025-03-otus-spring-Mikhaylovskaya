package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.Collections.singletonMap;
import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
@Repository
public class JdbcBookRepository implements BookRepository {

    private static final String FIND_ALL =
            """
            SELECT b.id as book_id, b.title as book_title,
                a.id as author_id, a.full_name as author_full_name,
                g.id as genre_id, g.name as genre_name
            FROM books AS b
                LEFT JOIN authors AS a on b.author_id = a.id
                LEFT JOIN genres AS g on b.genre_id = g.id
            """;

    private static final String FIND_BY_ID = FIND_ALL.concat(
            """
            WHERE b.id = :id
            """);

    private static final String DELETE_BY_ID =
            """
            DELETE FROM books WHERE id = :id
            """;

    private static final String INSERT =
            """
            INSERT INTO books (title, author_id, genre_id)
            VALUES (:title, :authorId, :genreId)
            """;

    private static final String UPDATE =
            """
            UPDATE books
            SET title = :title, author_id = :authorId, genre_id = :genreId
            WHERE id = :id
            """;

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Override
    public Optional<Book> findById(long id) {
        return namedParameterJdbcOperations.queryForStream(
                FIND_BY_ID,
                singletonMap("id", id),
                new BookRowMapper())
                .findFirst();
    }

    @Override
    public List<Book> findAll() {
        return namedParameterJdbcOperations.query(
                FIND_ALL,
                new BookRowMapper());
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        namedParameterJdbcOperations.update(
                DELETE_BY_ID,
                singletonMap("id", id));
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();
        var params = new MapSqlParameterSource(Map.of(
                "title", book.getTitle(),
                "authorId", book.getAuthor().getId(),
                "genreId", book.getGenre().getId()
                ));

        namedParameterJdbcOperations.update(
                INSERT,
                params,
                keyHolder,
                new String[] {"id"});
        book.setId(requireNonNull(keyHolder.getKeyAs(Long.class)));
        return book;
    }

    private Book update(Book book) {
        var updatedCount = namedParameterJdbcOperations.update(
                UPDATE,
                Map.of(
                        "id", book.getId(),
                        "title", book.getTitle(),
                        "authorId", book.getAuthor().getId(),
                        "genreId", book.getGenre().getId()
                ));
        if (updatedCount == 0) {
            throw new EntityNotFoundException("Book with id = %d not updated".formatted(book.getId()));
        }
        return book;
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            var book = new Book();
            book.setId(resultSet.getLong("book_id"));
            book.setTitle(resultSet.getString("book_title"));
            long authorId = resultSet.getLong("author_id");
            if (authorId > 0) {
                book.setAuthor(
                        new Author(
                                authorId,
                                resultSet.getString("author_full_name")
                        )
                );
            }
            long genreId = resultSet.getLong("genre_id");
            if (genreId > 0) {
                book.setGenre(
                        new Genre(
                                genreId,
                                resultSet.getString("genre_name")
                        )
                );
            }
            return book;
        }
    }
}