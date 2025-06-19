package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.dto.Author;
import ru.otus.hw.dto.Genre;
import ru.otus.hw.models.Book;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.util.StringUtils.hasLength;

@DisplayName("Сервис для работы с книгами ")
@SpringBootTest
class BookServiceImplTest {

    @Autowired
    private BookService bookService;

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        List<Book> returnedBooks = bookService.findAll();
        assertThat(returnedBooks).isNotEmpty()
                .hasSizeGreaterThan(1)
                .allMatch(
                        book -> hasLength(book.getTitle()) &&
                                hasLength(book.getAuthor()) &&
                                hasLength(book.getGenre())
                );
    }

    @DisplayName("должен загружать книгу по id")
    @Test
    void shouldReturnCorrectBookById() {
        Book expectedBook = bookService.findAll().get(0);
        Optional<Book> returnedBook = bookService.findById(expectedBook.getId());
        assertThat(returnedBook).isNotEmpty().get()
                .usingRecursiveComparison().isEqualTo(expectedBook);
    }

    @DisplayName("должен возвращать список всех авторов")
    @Test
    void shouldReturnCorrectAuthors() {
        List<Author> returnedBooks = bookService.findAuthors();
        assertThat(returnedBooks).isNotEmpty()
                .hasSizeGreaterThan(1)
                .doesNotHaveDuplicates()
                .allMatch(author -> hasLength(author.getFullName()) && author.getFullName().matches(".*[aA]uthor.*"));
    }

    @DisplayName("должен возвращать список всех жанров")
    @Test
    void shouldReturnCorrectGenres() {
        List<Genre> returnedBooks = bookService.findGenres();
        assertThat(returnedBooks).isNotEmpty()
                .hasSizeGreaterThan(1)
                .doesNotHaveDuplicates()
                .allMatch(genre -> hasLength(genre.getName()) && genre.getName().matches(".*[gG]enre.*"));
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldInsertNewBook() {
        String bookTitle = "TestTitle";
        String bookAuthor = "TestAuthor";
        String bookGenre = "TestGenre";

        Book returnedBook = bookService.insert(bookTitle, bookAuthor, bookGenre);

        assertThat(returnedBook).isNotNull()
                .matches(
                        book -> hasLength(book.getId()) &&
                        book.getTitle().equals(bookTitle) &&
                        book.getAuthor().equals(bookAuthor) &&
                        book.getGenre().equals(bookGenre)
                );
        assertThat(bookService.findById(returnedBook.getId()))
                .isNotEmpty()
                .get()
                .matches(
                        book -> book.getTitle().equals(bookTitle) &&
                                book.getAuthor().equals(bookAuthor) &&
                                book.getGenre().equals(bookGenre)
                );
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldUpdateBook() {
        String updatedBookId = bookService.findAll().get(0).getId();
        String updatedBookTitle = "NewTitle";
        String updatedBookAuthor = "NewAuthor";
        String updatedBookGenre = "NewGenre";

        assertThat(bookService.findById(updatedBookId))
                .isNotEmpty()
                .get()
                .matches(
                        book -> !book.getTitle().equals(updatedBookTitle) &&
                                !book.getAuthor().equals(updatedBookAuthor) &&
                                !book.getGenre().equals(updatedBookGenre)
                );

        Book returnedBook = bookService.update(updatedBookId, updatedBookTitle, updatedBookAuthor, updatedBookGenre);

        assertThat(returnedBook).isNotNull()
                .matches(
                        book -> book.getId().equals(updatedBookId) &&
                                book.getTitle().equals(updatedBookTitle) &&
                                book.getAuthor().equals(updatedBookAuthor) &&
                                book.getGenre().equals(updatedBookGenre)
                );
        assertThat(bookService.findById(returnedBook.getId()))
                .isNotEmpty()
                .get()
                .matches(
                        book -> book.getTitle().equals(updatedBookTitle) &&
                                book.getAuthor().equals(updatedBookAuthor) &&
                                book.getGenre().equals(updatedBookGenre)
                );
    }

    @DisplayName("должен удалять имеющуюся книгу по id ")
    @Test
    void deleteById() {
        String deletedBookId = bookService.findAll().get(0).getId();

        Optional<Book> deletedBook = bookService.findById(deletedBookId);
        assertThat(deletedBook).isNotEmpty();

        bookService.deleteById(deletedBookId);

        deletedBook = bookService.findById(deletedBookId);
        assertThat(deletedBook).isEmpty();
    }
}