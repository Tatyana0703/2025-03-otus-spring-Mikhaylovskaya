package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Genre;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import static java.util.Collections.singletonMap;

@RequiredArgsConstructor
@Repository
public class JdbcGenreRepository implements GenreRepository {

    private static final String FIND_ALL =
            """
            SELECT id, name
            FROM genres
            """;

    private static final String FIND_BY_ID = FIND_ALL.concat(
            """
            WHERE id = :id
            """);

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Override
    public List<Genre> findAll() {
        return namedParameterJdbcOperations.query(
                FIND_ALL,
                new GenreRowMapper());
    }

    @Override
    public Optional<Genre> findById(long id) {
        return namedParameterJdbcOperations.queryForStream(
                FIND_BY_ID,
                singletonMap("id", id),
                new GenreRowMapper())
                .findFirst();
    }

    private static class GenreRowMapper implements RowMapper<Genre> {

        @Override
        public Genre mapRow(ResultSet resultSet, int i) throws SQLException {
            return new Genre(resultSet.getLong("id"),
                    resultSet.getString("name"));
        }
    }
}