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
import ru.otus.hw.services.CommentService;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Контроллер для работы с комментариями, методы с аннотацией PreAuthorize ")
@WebMvcTest(controllers = CommentController.class)
@Import(SecurityConfiguration.class)
class CommentPermissionsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean(name = "commentServiceImpl")
    private CommentService commentService;

    @Test
    @WithMockUser
    void shouldSuccessDeleteCommentIfPassPreAuthorize() throws Exception {
        var commentId = 1L;
        var bookId = 2L;
        CommentReadDto commentReadDto = new CommentReadDto(commentId, "text", bookId, 3L);
        when(commentService.checkCommentOwner(anyLong(), any(UserDetails.class))).thenReturn(true);
        when(commentService.findById(1L)).thenReturn(Optional.of(commentReadDto));
        mockMvc.perform(post("/comments/" + commentId +"/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/comments?bookId=" + bookId));
        verify(commentService, times(1)).checkCommentOwner(eq(commentId), any(UserDetails.class));
        verify(commentService, times(1)).findById(commentId);
        verify(commentService, times(1)).deleteById(commentId);
    }

    @Test
    @WithMockUser
    void shouldNotDeleteCommentIfFailPreAuthorize() throws Exception {
        var commentId = 1L;
        when(commentService.checkCommentOwner(anyLong(), any(UserDetails.class))).thenReturn(false);
        mockMvc.perform(post("/comments/"+ commentId + "/delete"))
                .andExpect(view().name("error/accessDeniedError"));
        verify(commentService, times(1)).checkCommentOwner(eq(commentId), any(UserDetails.class));
        verifyNoMoreInteractions(commentService);
    }

    @Test
    @WithMockUser
    void shouldSuccessUpdateCommentIfPassPreAuthorize() throws Exception {
        var commentId = 1L;
        var bookId = 2L;
        CommentCreateEditDto commentCreateEditDto = CommentCreateEditDto.builder().text("text updated").bookId(bookId).build();
        CommentReadDto commentReadDto = new CommentReadDto(commentId, commentCreateEditDto.getText(), commentCreateEditDto.getBookId(), 3L);
        when(commentService.checkCommentOwner(anyLong(), any(UserDetails.class))).thenReturn(true);
        when(commentService.update(anyLong(), any(CommentCreateEditDto.class), any())).thenReturn(commentReadDto);

        mockMvc.perform(post("/comments/"+ commentId + "/update")
                    .param(CommentCreateEditDto.Fields.text, commentReadDto.getText())
                    .param(CommentCreateEditDto.Fields.bookId, String.valueOf(commentCreateEditDto.getBookId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/comments?bookId=" + bookId));

        verify(commentService, times(1)).checkCommentOwner(eq(commentId), any(UserDetails.class));
        verify(commentService, times(1)).update(eq(commentId), eq(commentCreateEditDto), any(UserDetails.class));
    }

    @Test
    @WithMockUser
    void shouldNotUpdateCommentIfFailPreAuthorize() throws Exception {
        var commentId = 1L;
        var bookId = 2L;
        CommentCreateEditDto commentCreateEditDto = CommentCreateEditDto.builder().text("text updated").bookId(bookId).build();
        CommentReadDto commentReadDto = new CommentReadDto(commentId, commentCreateEditDto.getText(), commentCreateEditDto.getBookId(), 3L);
        when(commentService.checkCommentOwner(anyLong(), any(UserDetails.class))).thenReturn(false);

        mockMvc.perform(post("/comments/"+ commentId + "/update")
                        .param(CommentCreateEditDto.Fields.text, commentReadDto.getText())
                        .param(CommentCreateEditDto.Fields.bookId, String.valueOf(commentCreateEditDto.getBookId())))
                .andExpect(view().name("error/accessDeniedError"));

        verify(commentService, times(1)).checkCommentOwner(eq(commentId), any(UserDetails.class));
        verifyNoMoreInteractions(commentService);
    }
}
