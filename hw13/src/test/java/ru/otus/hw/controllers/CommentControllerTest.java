package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.config.SecurityConfiguration;
import ru.otus.hw.dto.CommentCreateEditDto;
import ru.otus.hw.dto.CommentReadDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.services.CommentService;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@DisplayName("Контроллер для работы с комментариями, методы без аннотации PreAuthorize ")
@WebMvcTest(controllers = CommentController.class)
@Import(SecurityConfiguration.class)
@WithMockUser
class CommentControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CommentService commentService;

    private final List<CommentReadDto> comments = List.of(
            new CommentReadDto(1L, "text 1", 10L, 11L),
            new CommentReadDto(2L, "text 2",  12L, 13L));

    @Test
    void shouldRenderListPageWithCorrectViewAndModelAttributes() throws Exception {
        when(commentService.findAllByBookId(1L)).thenReturn(comments);
        mvc.perform(get("/comments").queryParam("bookId", String.valueOf(1L)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("comment/list"))
                .andExpect(model().attribute("comments", comments));
    }

    @Test
    void shouldRenderEditPageWithCorrectViewAndModelAttributes() throws Exception {
        CommentReadDto comment = comments.get(0);
        when(commentService.findById(1L)).thenReturn(Optional.of(comment));
        mvc.perform(get("/comments/1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("comment/edit"))
                .andExpect(model().attribute("comment", comment));
    }

    @Test
    void shouldRenderErrorPageWhenCommentNotFound() throws Exception {
        when(commentService.findById(1L)).thenThrow(new NotFoundException(anyString()));
        mvc.perform(get("/comments/1"))
                .andExpect(view().name("error/customError"));
    }

    @Test
    void shouldSaveCommentAndRedirectToContextPath() throws Exception {
        CommentReadDto comment = comments.get(0);
        when(commentService.create(any(CommentCreateEditDto.class), any(UserDetails.class))).thenReturn(comment);
        mvc.perform(post("/comments")
                        .param(CommentCreateEditDto.Fields.text, "comment text")
                        .param(CommentCreateEditDto.Fields.bookId, String.valueOf(comment.getBookId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/comments?bookId=" + comment.getBookId()));
        verify(commentService, times(1))
                .create(eq(CommentCreateEditDto.builder().text("comment text").bookId(comment.getBookId()).build()), any(UserDetails.class));
    }

    @Test
    void shouldRenderRegistrationPageWhenCreateParamsNotValid() throws Exception {
        mvc.perform(post("/comments")
                        .param(CommentCreateEditDto.Fields.text, "")
                        .param(CommentCreateEditDto.Fields.bookId, String.valueOf(10L)))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/comments/registration?bookId=10"));
        verifyNoInteractions(commentService);
    }
}
