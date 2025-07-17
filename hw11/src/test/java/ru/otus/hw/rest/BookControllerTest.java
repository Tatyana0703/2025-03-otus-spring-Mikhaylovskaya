package ru.otus.hw.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorReadDto;
import ru.otus.hw.dto.BookCreateEditDto;
import ru.otus.hw.dto.BookReadDto;
import ru.otus.hw.dto.GenreReadDto;
import ru.otus.hw.services.BookService;
import java.time.Duration;
import java.util.List;

@DisplayName("Контроллер для работы с книгами ")
@WebFluxTest(BookController.class)
class BookControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockitoBean
    private BookService bookService;

    private final List<AuthorReadDto> authors = List.of(
            AuthorReadDto.builder().id(1L).fullName("FullName1").build(),
            AuthorReadDto.builder().id(2L).fullName("FullName2").build());
    private final List<GenreReadDto> genres = List.of(
            GenreReadDto.builder().id(1L).name("Genre1").build(),
            GenreReadDto.builder().id(2L).name("Genre2").build());
    private final List<BookReadDto> books = List.of(
            BookReadDto.builder().id(1L).title("TestTitle1").author(authors.get(0)).genre(genres.get(0)).build(),
            BookReadDto.builder().id(2L).title("TestTitle2").author(authors.get(1)).genre(genres.get(1)).build());

    @Test
    void shouldReturnAllBooks() {
        when(bookService.findAll()).thenReturn(Flux.fromIterable(books));
        List<BookReadDto>  returnedBooks = webClient.get().uri("/api/books")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(BookReadDto.class)
                .getResponseBody()
                .timeout(Duration.ofSeconds(3))
                .collectList()
                .block();
        assertThat(returnedBooks)
                .hasSize(books.size())
                .containsExactlyInAnyOrderElementsOf(books);
        Mockito.verify(bookService, times(1)).findAll();
    }

    @Test
    void shouldGetBookById() {
        BookReadDto book = books.get(0);
        when(bookService.findById(1L)).thenReturn(Mono.just(book));
        BookReadDto returnedBook = webClient.get().uri("/api/books/1")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookReadDto.class)
                .returnResult()
                .getResponseBody();
        assertThat(returnedBook).isNotNull()
                .isEqualTo(book);
        Mockito.verify(bookService, times(1)).findById(1L);
    }

    @Test
    void shouldReturnErrorWhenBookNotFound() {
        when(bookService.findById(1L)).thenReturn(Mono.empty());
        webClient.get().uri("/api/books/1")
                .exchange()
                .expectStatus().isNotFound();
        Mockito.verify(bookService, times(1)).findById(1L);
    }

    @Test
    void shouldSaveBook() {
        AuthorReadDto author = authors.get(0);
        GenreReadDto genre = genres.get(0);
        BookCreateEditDto bookCreateEditDto = BookCreateEditDto.builder()
                .title("Book Test Title")
                .authorId(author.getId())
                .genreId(genre.getId())
                .build();
        BookReadDto bookReadDto = BookReadDto.builder()
                .id(1L)
                .title(bookCreateEditDto.getTitle())
                .author(author)
                .genre(genre)
                .build();
        when(bookService.create(bookCreateEditDto)).thenReturn(Mono.just(bookReadDto));

        BookReadDto returnedBook = webClient.post().uri("/api/books")
                .contentType(APPLICATION_JSON)
                .bodyValue(bookCreateEditDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BookReadDto.class)
                .returnResult()
                .getResponseBody();
        assertThat(returnedBook).isNotNull()
                .isEqualTo(bookReadDto);
        Mockito.verify(bookService, times(1)).create(bookCreateEditDto);
    }

    @Test
    void shouldReturnErrorWhenCreateParamsNotValid() {
        BookCreateEditDto bookCreateEditDto = BookCreateEditDto.builder()
                .title("")
                .authorId(null)
                .genreId(null)
                .build();
        webClient.post().uri("/api/books")
                .contentType(APPLICATION_JSON)
                .bodyValue(bookCreateEditDto)
                .exchange()
                .expectStatus().isBadRequest();
        Mockito.verifyNoInteractions(bookService);
    }

    @Test
    void shouldUpdateBook() {
        AuthorReadDto author = authors.get(0);
        GenreReadDto genre = genres.get(0);
        BookCreateEditDto bookCreateEditDto = BookCreateEditDto.builder()
                .title("Book Test Title")
                .authorId(author.getId())
                .genreId(genre.getId())
                .build();
        BookReadDto bookReadDto = BookReadDto.builder()
                .id(1L)
                .title(bookCreateEditDto.getTitle())
                .author(author)
                .genre(genre)
                .build();
        when(bookService.update(1L, bookCreateEditDto)).thenReturn(Mono.just(bookReadDto));

        BookReadDto returnedBook = webClient.put().uri("/api/books/1")
                .contentType(APPLICATION_JSON)
                .bodyValue(bookCreateEditDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookReadDto.class)
                .returnResult()
                .getResponseBody();
        assertThat(returnedBook).isNotNull()
                .isEqualTo(bookReadDto);
        Mockito.verify(bookService, times(1)).update(1L, bookCreateEditDto);
    }

    @Test
    void shouldReturnErrorWhenUpdatedBookParamsNotValid() throws Exception {
        BookCreateEditDto bookCreateEditDto = BookCreateEditDto.builder()
                .title("")
                .authorId(null)
                .genreId(null)
                .build();
        webClient.put().uri("/api/books/1")
                .contentType(APPLICATION_JSON)
                .bodyValue(bookCreateEditDto)
                .exchange()
                .expectStatus().isBadRequest();
        Mockito.verifyNoInteractions(bookService);
    }

    @Test
    void shouldDeleteBook() {
        when(bookService.deleteById(1L)).thenReturn(Mono.empty());
        webClient.delete().uri("/api/books/1")
                .exchange()
                .expectStatus().isNoContent();
        Mockito.verify(bookService, times(1)).deleteById(1L);
    }
}