package ru.otus.hw.controllers;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.otus.hw.config.SecurityConfiguration;
import ru.otus.hw.dto.CommentCreateEditDto;
import ru.otus.hw.dto.CommentReadDto;
import ru.otus.hw.services.CommentService;
import java.util.Optional;
import java.util.stream.Stream;
import static java.util.Objects.nonNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Контроллер для работы с комментариями, методы с аннотацией PreAuthorize ")
@WebMvcTest(controllers = CommentController.class)
@Import(SecurityConfiguration.class)
class CommentPermissionsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean(name = "commentServiceImpl")
    private CommentService commentService;

    @ParameterizedTest(name = "url {0} for user {1} should return {3} status")
    @MethodSource("getData")
    void shouldReturnExpectedStatus(String url, String userName, String[] roles,
                                    int status, String redirectedUrl, boolean isCommentOwner) throws Exception {
        var request = MockMvcRequestBuilders.post(url);
        if (nonNull(userName)) {
            request = request.with(user(userName).roles(roles));
        }

        if (isCommentOwner) {
            when(commentService.getCommentAuthorNameById(anyLong())).thenReturn(userName);
        } else {
            when(commentService.getCommentAuthorNameById(anyLong())).thenReturn(null);
        }
        CommentReadDto commentReadDto = mock(CommentReadDto.class);
        if (url.endsWith("/delete")) {
            when(commentService.findById(anyLong())).thenReturn(Optional.of(commentReadDto));
        }
        if (url.endsWith("/update")) {
            CommentCreateEditDto commentCreateEditDto = CommentCreateEditDto.builder().bookId(1L).text("comment").build();
            when(commentService.update(anyLong(), eq(commentCreateEditDto), any())).thenReturn(commentReadDto);
        }

        ResultActions resultActions = mockMvc.perform(request).andExpect(status().is(status));
        if (nonNull(redirectedUrl)) {
            resultActions.andExpect(view().name(Matchers.matchesRegex(redirectedUrl)));
        }
    }

    private static Stream<Arguments> getData() {
        var username = "username";
        var validRole = "TEST1";
        return Stream.of(
                Arguments.of("/comments/1/delete", username, new String[] {}, 403, null, false),
                Arguments.of("/comments/1/delete", username, new String[] {validRole}, 302, "redirect:/comments\\?bookId=\\d+", true),
                Arguments.of("/comments/1/delete", username, new String[] {validRole}, 403, "error/accessDeniedError", false),
                Arguments.of("/comments/1/update", username, new String[] {}, 403, null, false),
                Arguments.of("/comments/1/update", username, new String[] {validRole}, 302, "redirect:/comments/\\d+", true),
                Arguments.of("/comments/1/update", username, new String[] {validRole}, 403, "error/accessDeniedError", false));
    }
}
