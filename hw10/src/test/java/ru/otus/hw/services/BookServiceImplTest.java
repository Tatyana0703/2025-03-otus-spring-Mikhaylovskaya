package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

    private static final long SEARCHED_BOOK_ID = 1L;
    private static final long DELETED_BOOK_ID = 2L;
    private static final long UPDATED_BOOK_ID = 3L;
    private static final long AUTHOR_ID = 1L;
    private static final long GENRE_ID = 1L;

    @DisplayName("должен загружать книгу по id")
    @Test
    void shouldReturnCorrectBookById() {
        Optional<BookReadDto> returnedBook = bookService.findById(SEARCHED_BOOK_ID);
        assertThat(returnedBook).isNotEmpty().get()
                .matches(
                        book -> hasLength(book.getTitle()) &&
                                Objects.nonNull(book.getAuthor()) && hasLength(book.getAuthor().getFullName()) &&
                                Objects.nonNull(book.getGenre()) && hasLength(book.getGenre().getName())
                );
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        List<BookReadDto> returnedBooks = bookService.findAll();
        assertThat(returnedBooks).isNotEmpty()
                .hasSizeGreaterThan(1)
                .allMatch(
                        book -> hasLength(book.getTitle()) &&
                                Objects.nonNull(book.getAuthor()) && hasLength(book.getAuthor().getFullName()) &&
                                Objects.nonNull(book.getGenre()) && hasLength(book.getGenre().getName())
                );
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldInsertNewBook() {
        BookCreateEditDto bookDto = new BookCreateEditDto("BookTitle_Test", AUTHOR_ID, GENRE_ID);
        BookReadDto returnedBook = bookService.create(bookDto);

        assertThat(returnedBook).isNotNull()
                .matches(
                        book -> book.getId() > 0 &&
                                book.getTitle().equals(bookDto.getTitle()) &&
                                book.getAuthor().getId() == AUTHOR_ID &&
                                book.getGenre().getId() == GENRE_ID
                );
        assertThat(bookService.findById(returnedBook.getId()))
                .isNotEmpty()
                .get()
                .matches(
                        book -> book.getTitle().equals(bookDto.getTitle()) &&
                                book.getAuthor().getId() == AUTHOR_ID &&
                                book.getGenre().getId() == GENRE_ID
                );
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldUpdateBook() {
        BookCreateEditDto bookDto = new BookCreateEditDto("BookTitle_Updated", AUTHOR_ID, GENRE_ID);
        assertThat(bookService.findById(UPDATED_BOOK_ID))
                .isNotEmpty()
                .get()
                .matches(
                        book -> !book.getTitle().equals(bookDto.getTitle()) &&
                                book.getAuthor().getId() != AUTHOR_ID &&
                                book.getGenre().getId() != GENRE_ID
                );

        BookReadDto returnedBook = bookService.update(UPDATED_BOOK_ID, bookDto);

        assertThat(returnedBook)
                .matches(
                        book -> book.getId() == UPDATED_BOOK_ID &&
                                book.getTitle().equals(bookDto.getTitle()) &&
                                book.getAuthor().getId() == AUTHOR_ID &&
                                book.getGenre().getId() == GENRE_ID
                );
        assertThat(bookService.findById(returnedBook.getId()))
                .isNotEmpty()
                .get()
                .matches(
                        book -> book.getTitle().equals(bookDto.getTitle()) &&
                                book.getAuthor().getId() == AUTHOR_ID &&
                                book.getGenre().getId() == GENRE_ID
                );
    }

    @DisplayName("должен удалять имеющуюся книгу по id ")
    @Test
    void deleteById() {
        Optional<BookReadDto> deletedBook = bookService.findById(DELETED_BOOK_ID);
        assertThat(deletedBook).isNotEmpty();

        bookService.deleteById(DELETED_BOOK_ID);

        deletedBook = bookService.findById(DELETED_BOOK_ID);
        assertThat(deletedBook).isEmpty();
    }
}