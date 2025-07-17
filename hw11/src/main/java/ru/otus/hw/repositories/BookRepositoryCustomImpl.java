package ru.otus.hw.repositories;


import io.r2dbc.spi.Parameters;
import io.r2dbc.spi.R2dbcType;
import io.r2dbc.spi.Readable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorReadDto;
import ru.otus.hw.dto.BookReadDto;
import ru.otus.hw.dto.GenreReadDto;

@Repository
@RequiredArgsConstructor
public class BookRepositoryCustomImpl implements BookRepositoryCustom {

    private final R2dbcEntityTemplate template;

    public Flux<BookReadDto> findAllCustom() {
        return template.getDatabaseClient().inConnectionMany(connection ->
                Flux.from(connection.createStatement("""
                        SELECT b.id as book_id, b.title as book_title,
                            a.id as author_id, a.full_name as author_full_name,
                            g.id as genre_id, g.name as genre_name
                        FROM books AS b
                            LEFT JOIN authors AS a on b.author_id = a.id
                            LEFT JOIN genres AS g on b.genre_id = g.id
                        """).execute())
                        .flatMap(result -> result.map(this::mapper)));
    }

    public Mono<BookReadDto> findByIdCustom(Long id) {
        return template.getDatabaseClient().inConnection(connection ->
                Mono.from(connection.createStatement("""
                        SELECT b.id as book_id, b.title as book_title,
                            a.id as author_id, a.full_name as author_full_name,
                            g.id as genre_id, g.name as genre_name
                        FROM books AS b
                            LEFT JOIN authors AS a on b.author_id = a.id
                            LEFT JOIN genres AS g on b.genre_id = g.id
                        WHERE b.id = $1
                        """)
                        .bind(0, Parameters.in(R2dbcType.BIGINT, id))
                        .execute())
                        .flatMap(result -> Mono.from(result.map(this::mapper))));
    }

    private BookReadDto mapper(Readable selectedRecord) {
        AuthorReadDto authorDto = AuthorReadDto.builder()
                .id(selectedRecord.get("author_id", Long.class))
                .fullName(selectedRecord.get("author_full_name", String.class))
                .build();
        GenreReadDto genreDto = GenreReadDto.builder()
                .id(selectedRecord.get("genre_id", Long.class))
                .name(selectedRecord.get("genre_name", String.class))
                .build();
        return BookReadDto.builder()
                .id(selectedRecord.get("book_id", Long.class))
                .title(selectedRecord.get("book_title", String.class))
                .author(authorDto)
                .genre(genreDto)
                .build();
    }
}
