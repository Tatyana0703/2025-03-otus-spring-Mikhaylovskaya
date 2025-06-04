package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.hw.models.Genre;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с жанрами ")
@DataJpaTest
class GenreRepositoryTest {

    @Autowired
    private GenreRepository genreRepository;

    private static final int GENRES_COUNT = 3;
    private static final long GENRE_ID = 1L;
    private static final String GENRE_NAME = "Test_Genre_1";

    @DisplayName("должен загружать жанр по id")
    @Test
    void shouldReturnCorrectGenreById() {
        Optional<Genre> genre = genreRepository.findById(GENRE_ID);
        assertThat(genre).isNotEmpty().get()
                .hasFieldOrPropertyWithValue("name", GENRE_NAME);
    }

    @DisplayName("должен загружать список всех жанров")
    @Test
    void shouldReturnCorrectGenresList() {
        List<Genre> genres = genreRepository.findAll();
        assertThat(genres).hasSize(GENRES_COUNT);
    }
}