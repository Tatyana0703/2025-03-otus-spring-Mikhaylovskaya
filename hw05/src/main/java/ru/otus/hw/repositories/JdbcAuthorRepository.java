package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import static java.util.Collections.singletonMap;

@RequiredArgsConstructor
@Repository
public class JdbcAuthorRepository implements AuthorRepository {

    private static final String FIND_ALL =
            """
            SELECT id, full_name
            FROM authors
            """;

    private static final String FIND_BY_ID = FIND_ALL.concat(
            """
            WHERE id = :id
            """);

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Override
    public List<Author> findAll() {
        return namedParameterJdbcOperations.query(
                FIND_ALL,
                new AuthorRowMapper());
    }

    @Override
    public Optional<Author> findById(long id) {
        return namedParameterJdbcOperations.queryForStream(
                FIND_BY_ID,
                singletonMap("id", id),
                new AuthorRowMapper())
                .findFirst();
    }

    private static class AuthorRowMapper implements RowMapper<Author> {

        @Override
        public Author mapRow(ResultSet resultSet, int i) throws SQLException {
            return new Author(resultSet.getLong("id"),
                    resultSet.getString("full_name"));
        }
    }
}