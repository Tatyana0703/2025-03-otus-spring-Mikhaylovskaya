package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.util.StringUtils.hasLength;

@DisplayName("Репозиторий на основе Jpa для работы с книгами ")
@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager em;

    private static final int BOOKS_COUNT = 3;
    private static final long BOOK_ID = 1L;
    private static final long AUTHOR_ID = 1L;
    private static final long GENRE_ID = 1L;
    private static final long UPDATED_AUTHOR_ID = 2L;
    private static final long UPDATED_GENRE_ID = 2L;

    @DisplayName("должен загружать книгу по id")
    @Test
    void shouldReturnCorrectBookById() {
        Optional<Book> actualBook = bookRepository.findBookWithAuthorAndGenre(BOOK_ID);
        Book expectedBook = em.find(Book.class, BOOK_ID);
        assertThat(actualBook).isPresent().get()
                .usingRecursiveComparison().isEqualTo(expectedBook);
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        List<Book> actualBooks = bookRepository.findAllBooksWithAuthorAndGenre();
        assertThat(actualBooks).isNotEmpty()
                .hasSize(BOOKS_COUNT)
                .allMatch(book -> hasLength(book.getTitle()));
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        Author author = em.find(Author.class, AUTHOR_ID);
        assertThat(author).isNotNull();
        Genre genre = em.find(Genre.class, GENRE_ID);
        assertThat(genre).isNotNull();
        Book expectedBook = Book.builder()
                .id(0L)
                .title("BookTitle_10500")
                .author(author)
                .genre(genre)
                .build();

        Book returnedBook = bookRepository.save(expectedBook);

        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);
        em.flush();
        assertThat(em.find(Book.class, returnedBook.getId()))
                .isNotNull()
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(returnedBook);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        Author expectedAuthor = em.find(Author.class, UPDATED_AUTHOR_ID);
        assertThat(expectedAuthor).isNotNull();
        Genre expectedGenre = em.find(Genre.class, UPDATED_GENRE_ID);
        assertThat(expectedGenre).isNotNull();
        Book expectedBook = Book.builder()
                .id(BOOK_ID)
                .title("BookTitle_10500")
                .author(expectedAuthor)
                .genre(expectedGenre)
                .build();
        assertThat(em.find(Book.class, BOOK_ID)).isNotNull().isNotEqualTo(expectedBook);

        Book returnedBook = bookRepository.save(expectedBook);

        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() == BOOK_ID)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);
        em.flush();
        assertThat(em.find(Book.class, returnedBook.getId()))
                .isNotNull()
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(returnedBook);
    }

    @DisplayName("должен удалять имеющуюся книгу по id ")
    @Test
    void shouldDeleteBook() {
        Book book = em.find(Book.class, BOOK_ID);
        assertThat(book).isNotNull();

        bookRepository.deleteById(BOOK_ID);

        em.flush();
        book = em.find(Book.class, BOOK_ID);
        assertThat(book).isNull();
    }
}