package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с авторами ")
@DataJpaTest
@Import(JpaAuthorRepository.class)
class JpaAuthorRepositoryTest {

    @Autowired
    private JpaAuthorRepository authorRepository;

    private static final int AUTHORS_COUNT = 3;
    private static final long AUTHOR_ID = 1L;
    private static final String AUTHOR_FULL_NAME = "Test_Author_1";

    @DisplayName("должен загружать автора по id")
    @Test
    void shouldReturnCorrectAuthorById() {
        Optional<Author> author = authorRepository.findById(AUTHOR_ID);
        assertThat(author).isNotEmpty().get()
                .hasFieldOrPropertyWithValue("fullName", AUTHOR_FULL_NAME);
    }

    @DisplayName("должен загружать список всех авторов")
    @Test
    void shouldReturnCorrectAuthorsList() {
        List<Author> authors = authorRepository.findAll();
        assertThat(authors).hasSize(AUTHORS_COUNT);
    }
}