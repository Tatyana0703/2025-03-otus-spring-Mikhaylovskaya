package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import ru.otus.hw.dto.AuthorReadDto;
import ru.otus.hw.dto.BookCreateEditDto;
import ru.otus.hw.dto.BookReadDto;
import ru.otus.hw.dto.GenreReadDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;
import java.util.List;
import java.util.Optional;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@DisplayName("Контроллер для работы с книгами ")
@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private GenreService genreService;

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
    void shouldRenderListPageWithCorrectViewAndModelAttributes() throws Exception {
        when(bookService.findAll()).thenReturn(books);
        mvc.perform(get("/books"))
                .andExpect(view().name("book/list"))
                .andExpect(model().attribute("books", books));
    }

    @Test
    void shouldRenderEditPageWithCorrectViewAndModelAttributes() throws Exception {
        BookReadDto book = books.get(0);
        when(bookService.findById(1L)).thenReturn(Optional.of(book));
        when(authorService.findAll()).thenReturn(authors);
        when(genreService.findAll()).thenReturn(genres);
        mvc.perform(get("/books/1"))
                .andExpect(view().name("book/edit"))
                .andExpect(model().attribute("book", book))
                .andExpect(model().attribute("authors", authors))
                .andExpect(model().attribute("genres", genres));
    }

    @Test
    void shouldRenderErrorPageWhenBookNotFound() throws Exception {
        when(bookService.findById(1L)).thenThrow(new NotFoundException(anyString()));
        mvc.perform(get("/books/1"))
                .andExpect(view().name("error/customError"));
    }

    @Test
    void shouldSaveBookAndRedirectToContextPath() throws Exception {
        mvc.perform(post("/books")
                        .param(BookCreateEditDto.Fields.title, "Book Test Title")
                        .param(BookCreateEditDto.Fields.authorId, String.valueOf(authors.get(0).getId()))
                        .param(BookCreateEditDto.Fields.genreId, String.valueOf(genres.get(0).getId())))
                .andExpect(view().name("redirect:/books"));
        verify(bookService, times(1))
                .create(new BookCreateEditDto("Book Test Title", authors.get(0).getId(), genres.get(0).getId()));
    }

    @Test
    void shouldRenderRegistrationPageWhenCreateParamsNotValid() throws Exception {
        mvc.perform(post("/books")
                        .param(BookCreateEditDto.Fields.title, ""))
                .andExpect(view().name("redirect:/books/registration"));
        verifyNoInteractions(bookService);
    }

    @Test
    void shouldUpdateBookAndRedirectToContextPath() throws Exception {
        BookReadDto book = books.get(0);
        when(bookService.update(anyLong(), any(BookCreateEditDto.class))).thenReturn(Optional.of(book));
        mvc.perform(post("/books/1/update")
                        .param(BookCreateEditDto.Fields.title, "Updated Book Test Title")
                        .param(BookCreateEditDto.Fields.authorId, String.valueOf(authors.get(0).getId()))
                        .param(BookCreateEditDto.Fields.genreId, String.valueOf(genres.get(1).getId())))
                .andExpect(view().name("redirect:/books"));
        verify(bookService, times(1))
                .update(1L, new BookCreateEditDto("Updated Book Test Title",
                        authors.get(0).getId(), genres.get(1).getId()));
    }

    @Test
    void shouldRenderEditPageWhenUpdateParamsNotValid() throws Exception {
        mvc.perform(post("/books/1/update")
                        .param(BookCreateEditDto.Fields.title, ""))
                .andExpect(view().name("redirect:/books/1"));
        verifyNoInteractions(bookService);
    }

    @Test
    void shouldDeleteBookAndRedirectToContextPath() throws Exception {
        mvc.perform(post("/books/1/delete"))
                .andExpect(view().name("redirect:/books"));
        verify(bookService, times(1)).deleteById(1L);
    }
}