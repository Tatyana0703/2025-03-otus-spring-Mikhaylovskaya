package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.util.StringUtils.hasLength;

@DisplayName("Сервис для работы с книгами ")
@SpringBootTest
@Transactional(propagation = Propagation.NEVER)
class BookServiceImplTest {

    @Autowired
    private BookService bookService;

    private static final long SEARCHED_BOOK_ID = 1L;
    private static final long DELETED_BOOK_ID = 2L;
    private static final long UPDATED_BOOK_ID = 3L;
    private static final String UPDATED_BOOK_TITLE = "Title updated";
    private static final long AUTHOR_ID = 1L;
    private static final long GENRE_ID = 1L;

    @DisplayName("должен загружать книгу по id")
    @Test
    void shouldReturnCorrectBookById() {
        Book expectedBook = getExpectedSearchedBook();
        Optional<Book> book = bookService.findById(SEARCHED_BOOK_ID);
        assertThat(book).isNotEmpty().get()
                .usingRecursiveComparison().isEqualTo(expectedBook);
    }

    private Book getExpectedSearchedBook() {
        return Book.builder()
                .id(SEARCHED_BOOK_ID)
                .title("Test_BookTitle_1")
                .author(new Author(1L, "Test_Author_1"))
                .genre(new Genre(1L, "Test_Genre_1"))
                .build();
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        List<Book> actualBooks = bookService.findAll();
        assertThat(actualBooks).isNotEmpty()
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
        var bookTitle = "BookTitle_10500";
        var returnedBook = bookService.insert(bookTitle, AUTHOR_ID, GENRE_ID);

        assertThat(returnedBook).isNotNull()
                .matches(
                        book -> book.getId() > 0 &&
                        book.getTitle().equals(bookTitle) &&
                        book.getAuthor().getId() == AUTHOR_ID &&
                        book.getGenre().getId() == GENRE_ID
                );
        assertThat(bookService.findById(returnedBook.getId()))
                .isNotEmpty()
                .get()
                .matches(
                        book -> book.getTitle().equals(bookTitle) &&
                                book.getAuthor().getId() == AUTHOR_ID &&
                                book.getGenre().getId() == GENRE_ID
                );
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldUpdateBook() {
        assertThat(bookService.findById(UPDATED_BOOK_ID))
                .isNotEmpty()
                .get()
                .matches(
                        book -> !book.getTitle().equals(UPDATED_BOOK_TITLE) &&
                                book.getAuthor().getId() != AUTHOR_ID &&
                                book.getGenre().getId() != GENRE_ID
                );

        var returnedBook = bookService.update(UPDATED_BOOK_ID, UPDATED_BOOK_TITLE, AUTHOR_ID, GENRE_ID);

        assertThat(returnedBook).isNotNull()
                .matches(
                        book -> book.getId() == UPDATED_BOOK_ID &&
                                book.getTitle().equals(UPDATED_BOOK_TITLE) &&
                                book.getAuthor().getId() == AUTHOR_ID &&
                                book.getGenre().getId() == GENRE_ID
                );
        assertThat(bookService.findById(returnedBook.getId()))
                .isNotEmpty()
                .get()
                .matches(
                        book -> book.getTitle().equals(UPDATED_BOOK_TITLE) &&
                                book.getAuthor().getId() == AUTHOR_ID &&
                                book.getGenre().getId() == GENRE_ID
                );
    }

    @DisplayName("должен удалять имеющуюся книгу по id ")
    @Test
    void deleteById() {
        Optional<Book> deletedBook = bookService.findById(DELETED_BOOK_ID);
        assertThat(deletedBook).isNotEmpty();

        bookService.deleteById(DELETED_BOOK_ID);

        deletedBook = bookService.findById(DELETED_BOOK_ID);
        assertThat(deletedBook).isEmpty();
    }
}