package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.otus.hw.dto.BookCreateEditDto;
import ru.otus.hw.dto.BookReadDto;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.util.StringUtils.hasLength;

@DisplayName("Сервис для работы с книгами ")
@SpringBootTest
class BookServiceImplTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private GenreService genreService;

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        List<BookReadDto> returnedBooks = bookService.findAll();
        assertThat(returnedBooks).isNotEmpty()
                .hasSize(3)
                .allMatch(
                        book -> hasLength(book.getTitle()) &&
                                Objects.nonNull(book.getAuthor()) && hasLength(book.getAuthor().getFullName()) &&
                                Objects.nonNull(book.getGenre()) && hasLength(book.getGenre().getName())
                );
    }

    @DisplayName("должен загружать книгу по id")
    @Test
    void shouldReturnCorrectBookById() {
        long searchedBookId = bookService.findAll().get(0).getId();
        Optional<BookReadDto> returnedBook = bookService.findById(searchedBookId);
        assertThat(returnedBook).isNotEmpty().get()
                .matches(
                        book -> hasLength(book.getTitle()) &&
                                Objects.nonNull(book.getAuthor()) && hasLength(book.getAuthor().getFullName()) &&
                                Objects.nonNull(book.getGenre()) && hasLength(book.getGenre().getName())
                );
    }

    @Sql(scripts = "classpath:test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldInsertNewBook() {
        long authorId = authorService.findAll().get(0).getId();
        long genreId = genreService.findAll().get(0).getId();
        BookCreateEditDto bookDto = new BookCreateEditDto("BookTitle_Test", authorId, genreId);
        BookReadDto returnedBook = bookService.create(bookDto);

        assertThat(returnedBook).isNotNull()
                .matches(
                        book -> book.getId() > 0 &&
                                book.getTitle().equals(bookDto.getTitle()) &&
                                book.getAuthor().getId() == authorId &&
                                book.getGenre().getId() == genreId
                );
        assertThat(bookService.findById(returnedBook.getId()))
                .isNotEmpty()
                .get()
                .matches(
                        book -> book.getTitle().equals(bookDto.getTitle()) &&
                                book.getAuthor().getId() == authorId &&
                                book.getGenre().getId() == genreId
                );
    }

    @Sql(scripts = "classpath:test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldUpdateBook() {
        BookReadDto updatedBook = bookService.findAll().get(0);
        long updatedBookId = updatedBook.getId();
        long authorId = authorService.findAll().stream()
                .filter(author -> author.getId() != updatedBook.getAuthor().getId())
                .findFirst()
                .get()
                .getId();
        long genreId = genreService.findAll().stream()
                .filter(genre -> genre.getId() != updatedBook.getGenre().getId())
                .findFirst()
                .get()
                .getId();
        BookCreateEditDto bookDto = new BookCreateEditDto("BookTitle_Updated", authorId, genreId);
        assertThat(bookService.findById(updatedBookId))
                .isNotEmpty()
                .get()
                .matches(
                        book -> !book.getTitle().equals(bookDto.getTitle()) &&
                                book.getAuthor().getId() != authorId &&
                                book.getGenre().getId() != genreId
                );

        BookReadDto returnedBook = bookService.update(updatedBookId, bookDto);

        assertThat(returnedBook).matches(
                book -> book.getId() == updatedBookId &&
                        book.getTitle().equals(bookDto.getTitle()) &&
                        book.getAuthor().getId() == authorId &&
                        book.getGenre().getId() == genreId);
        assertThat(bookService.findById(returnedBook.getId()))
                .isNotEmpty()
                .get()
                .matches(
                        book -> book.getTitle().equals(bookDto.getTitle()) &&
                                book.getAuthor().getId() == authorId &&
                                book.getGenre().getId() == genreId
                );
    }

    @Sql(scripts = "classpath:test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("должен удалять имеющуюся книгу по id ")
    @Test
    void deleteById() {
        long deletedBookId = bookService.findAll().get(0).getId();
        Optional<BookReadDto> deletedBook = bookService.findById(deletedBookId);
        assertThat(deletedBook).isNotEmpty();

        bookService.deleteById(deletedBookId);

        deletedBook = bookService.findById(deletedBookId);
        assertThat(deletedBook).isEmpty();
    }
}