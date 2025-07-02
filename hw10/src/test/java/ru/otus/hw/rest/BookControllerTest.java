package ru.otus.hw.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import ru.otus.hw.dto.AuthorReadDto;
import ru.otus.hw.dto.BookCreateEditDto;
import ru.otus.hw.dto.BookReadDto;
import ru.otus.hw.dto.GenreReadDto;
import ru.otus.hw.services.BookService;
import java.util.List;
import java.util.Optional;

@DisplayName("Контроллер для работы с книгами ")
@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private BookService bookService;

    private List<AuthorReadDto> authors = List.of(
            new AuthorReadDto(1L, "FullName1"),
            new AuthorReadDto(2L, "FullName2"));
    private List<GenreReadDto> genres = List.of(
            new GenreReadDto(1L, "Genre1"),
            new GenreReadDto(2L, "Genre2"));
    private List<BookReadDto> books = List.of(
            new BookReadDto(1L, "TestTitle1", authors.get(0), genres.get(0)),
            new BookReadDto(2L, "TestTitle2",  authors.get(1), genres.get(1)));

    @Test
    void shouldReturnAllBooks() throws Exception {
        when(bookService.findAll()).thenReturn(books);
        mvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(books)));
    }

    @Test
    void shouldGetBookById() throws Exception {
        BookReadDto book = books.get(0);
        when(bookService.findById(1L)).thenReturn(Optional.of(book));
        mvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(book)));
    }

    @Test
    void shouldReturnErrorWhenBookNotFound() throws Exception {
        when(bookService.findById(1L)).thenReturn(Optional.empty());
        mvc.perform(get("/api/books/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSaveBook() throws Exception {
        BookCreateEditDto bookCreateEditDto =
                new BookCreateEditDto("Book Test Title", authors.get(0).getId(), genres.get(0).getId());
        BookReadDto bookReadDto = new BookReadDto(100, "Book Test Title", authors.get(0), genres.get(0));
        when(bookService.create(bookCreateEditDto)).thenReturn(bookReadDto);

        mvc.perform(post("/api/books")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookCreateEditDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookReadDto)));
        Mockito.verify(bookService, times(1)).create(bookCreateEditDto);
    }

    @Test
    void shouldReturnErrorWhenCreateParamsNotValid() throws Exception {
        BookCreateEditDto bookCreateEditDto =
                new BookCreateEditDto("", null, null);
        mvc.perform(post("/api/books")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookCreateEditDto)))
                .andExpect(status().isBadRequest());
        Mockito.verifyNoInteractions(bookService);
    }

    @Test
    void shouldUpdateBook() throws Exception {
        BookCreateEditDto bookCreateEditDto =
                new BookCreateEditDto("Book Test Title", authors.get(0).getId(), genres.get(0).getId());
        BookReadDto bookReadDto = new BookReadDto(100, "Book Test Title", authors.get(0), genres.get(0));
        when(bookService.update(1L, bookCreateEditDto)).thenReturn(bookReadDto);

        mvc.perform(put("/api/books/1")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookCreateEditDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookReadDto)));
        Mockito.verify(bookService, times(1)).update(1L, bookCreateEditDto);
    }

    @Test
    void shouldReturnErrorWhenUpdatedBookParamsNotValid() throws Exception {
        BookCreateEditDto bookCreateEditDto =
                new BookCreateEditDto("", null, null);
        mvc.perform(put("/api/books/1")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookCreateEditDto)))
                .andExpect(status().isBadRequest());
        Mockito.verifyNoInteractions(bookService);
    }

    @Test
    void shouldDeleteBook() throws Exception {
        mvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());
        Mockito.verify(bookService, times(1)).deleteById(1L);
    }
}