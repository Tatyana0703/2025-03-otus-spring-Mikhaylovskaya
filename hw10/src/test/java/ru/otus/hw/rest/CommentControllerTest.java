package ru.otus.hw.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.*;
import ru.otus.hw.services.CommentService;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Контроллер для работы с книгами ")
@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private CommentService commentService;

    private final List<CommentReadDto> comments = List.of(
            new CommentReadDto(1L, "TestComment1", 10L),
            new CommentReadDto(2L, "TestComment2", 10L));

    @Test
    void shouldReturnAllCommentByBookId() throws Exception {
        when(commentService.findAllByBookId(1L)).thenReturn(comments);
        mvc.perform(get("/api/comments/book/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(comments)));
        Mockito.verify(commentService, times(1)).findAllByBookId(anyLong());
    }

    @Test
    void shouldGetCommentById() throws Exception {
        CommentReadDto comment = comments.get(0);
        when(commentService.findById(1L)).thenReturn(Optional.of(comment));
        mvc.perform(get("/api/comments/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(comment)));
        Mockito.verify(commentService, times(1)).findById(anyLong());
    }

    @Test
    void shouldReturnErrorWhenCommentNotFound() throws Exception {
        when(commentService.findById(1L)).thenReturn(Optional.empty());
        mvc.perform(get("/api/comments/1"))
                .andExpect(status().isNotFound());
        Mockito.verify(commentService, times(1)).findById(anyLong());
    }

    @Test
    void shouldSaveComment() throws Exception {
        CommentCreateEditDto commentCreateEditDto = new CommentCreateEditDto("Comment Text", 100L);
        CommentReadDto commentReadDto = new CommentReadDto(10, "Comment Text", 100L);
        when(commentService.create(commentCreateEditDto)).thenReturn(commentReadDto);

        mvc.perform(post("/api/comments")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentCreateEditDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(commentReadDto)));
        Mockito.verify(commentService, times(1)).create(commentCreateEditDto);
    }

    @Test
    void shouldReturnErrorWhenCreateParamsNotValid() throws Exception {
        CommentCreateEditDto commentCreateEditDto = new CommentCreateEditDto("", null);
        mvc.perform(post("/api/comments")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentCreateEditDto)))
                .andExpect(status().isBadRequest());
        Mockito.verifyNoInteractions(commentService);
    }

    @Test
    void shouldUpdateComment() throws Exception {
        CommentCreateEditDto commentCreateEditDto = new CommentCreateEditDto("Comment Text", 100L);
        CommentReadDto commentReadDto = new CommentReadDto(1L, "Comment Text", 100L);
        when(commentService.update(1L, commentCreateEditDto)).thenReturn(commentReadDto);

        mvc.perform(put("/api/comments/1")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentCreateEditDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(commentReadDto)));
        Mockito.verify(commentService, times(1)).update(1L, commentCreateEditDto);
    }

    @Test
    void shouldReturnErrorWhenUpdatedBookParamsNotValid() throws Exception {
        CommentCreateEditDto commentCreateEditDto = new CommentCreateEditDto("", null);
        mvc.perform(put("/api/comments/1")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentCreateEditDto)))
                .andExpect(status().isBadRequest());
        Mockito.verifyNoInteractions(commentService);
    }

    @Test
    void shouldDeleteComment() throws Exception {
        mvc.perform(delete("/api/comments/1"))
                .andExpect(status().isNoContent());
        Mockito.verify(commentService, times(1)).deleteById(1L);
    }
}