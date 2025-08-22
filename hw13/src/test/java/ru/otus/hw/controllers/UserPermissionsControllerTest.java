package ru.otus.hw.controllers;

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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.otus.hw.config.SecurityConfiguration;
import ru.otus.hw.dto.BookCreateEditDto;
import ru.otus.hw.dto.CommentCreateEditDto;
import ru.otus.hw.services.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;
import static java.util.Objects.nonNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;

@DisplayName("Контроллер для работы с пользователями ")
@WebMvcTest(controllers = {UserController.class, BookController.class, AuthorController.class,
        GenreController.class, CommentController.class})
@Import(SecurityConfiguration.class)
class UserPermissionsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private RoleService roleService;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private CommentService commentService;

    private MockHttpServletRequestBuilder methodRequestBuilder(String method, String url) {
        Map<String, Function<String, MockHttpServletRequestBuilder>> methodMap = Map.of(
                "get", MockMvcRequestBuilders::get,
                "post", MockMvcRequestBuilders::post);
        return methodMap.get(method).apply(url);
    }

    @ParameterizedTest(name = "method {0} {1} for user {2} should return {4} status")
    @MethodSource("getData")
    void shouldReturnExpectedStatus(String method, String url, String userName, String[] roles,
                                    int status, boolean checkLoginRedirection) throws Exception {
        var request = methodRequestBuilder(method, url);
        if (nonNull(userName)) {
            request = request.with(user(userName).roles(roles));
        }
        ResultActions resultActions = mockMvc.perform(request).andExpect(status().is(status));
        if (checkLoginRedirection) {
            resultActions.andExpect(redirectedUrlPattern("**/login"));
        }
    }

    private static Stream<Arguments> getData() {
        var username = "username";
        return Stream.of(
                Arguments.of("get", "/books", null, null, 302, true),
                Arguments.of("get", "/books", username, new String[] {}, 403, false),
                Arguments.of("get", "/books", username, new String[] {"TEMP"}, 403, false),
                Arguments.of("get", "/books", username, new String[] {"TEST1"}, 200, false),
                Arguments.of("get", "/books", username, new String[] {"TEST2"}, 200, false),
                Arguments.of("get", "/books", username, new String[] {"TEST1", "TEST2"}, 200, false),
                Arguments.of("get", "/books/registration", null, null, 302, true),
                Arguments.of("get", "/books/registration", username, new String[] {}, 403, false),
                Arguments.of("get", "/books/registration", username, new String[] {"TEST1"}, 200, false),
                Arguments.of("get", "/books/1", null, null, 302, true),
                Arguments.of("get", "/books/1", username, new String[] {}, 403, false),
                Arguments.of("get", "/books/1", username, new String[] {"TEST1"}, 400, false),
                Arguments.of("get", "/authors", null, null, 302, true),
                Arguments.of("get", "/authors", username, new String[] {}, 403, false),
                Arguments.of("get", "/authors", username, new String[] {"TEST1"}, 200, false),
                Arguments.of("get", "/genres", null, null, 302, true),
                Arguments.of("get", "/genres", username, new String[] {}, 403, false),
                Arguments.of("get", "/genres", username, new String[] {"TEST1"}, 200, false),
                Arguments.of("get", "/comments?bookId=1", null, null, 302, true),
                Arguments.of("get", "/comments?bookId=1", username, new String[] {"TEST1"}, 200, false),
                Arguments.of("get", "/comments/1", null, null, 302, true),
                Arguments.of("get", "/comments/1", username, new String[] {"TEST1"}, 400, false),
                Arguments.of("get", "/comments/registration?bookId=1", null, null, 302, true),
                Arguments.of("get", "/comments/registration?bookId=1", username, new String[] {"TEST1"}, 200, false),
                Arguments.of("get", "/login", null, null, 200, false),
                Arguments.of("get", "/users/registration", null, null, 200, false),
                Arguments.of("post", "/books", null, null, 302, true),
                Arguments.of("post", "/books/1/update", null, null, 302, true),
                Arguments.of("post", "/books/1/delete", null, null, 302, true),
                Arguments.of("post", "/comments", null, null, 302, true),
                Arguments.of("post", "/comments/1/update", null, null, 302, true),
                Arguments.of("post", "/comments/1/delete", null, null, 302, true)
        );
    }

    @ParameterizedTest(name = "method get {0} for user {1} should return {3} status")
    @MethodSource("getDataForMethodWithParams")
    void shouldReturnExpectedStatusForPostMethod(String url, Map<String, String> params,
                                                     String userName, String[] roles,
                                                     int status, String redirectedUrl) throws Exception {
        MockHttpServletRequestBuilder request = methodRequestBuilder("post", url);
        params.forEach(request::param);
        if (nonNull(userName)) {
            request = request.with(user(userName).roles(roles));
        }
        ResultActions resultActions = mockMvc.perform(request).andExpect(status().is(status));
        if (nonNull(redirectedUrl)) {
            resultActions.andExpect(redirectedUrl(redirectedUrl));
        }
    }

    private static Stream<Arguments> getDataForMethodWithParams() {
        var username = "username";
        var validBookParams = Map.of(BookCreateEditDto.Fields.title, "title",
                BookCreateEditDto.Fields.authorId, "1",
                BookCreateEditDto.Fields.genreId, "1");
        var validCommentParams = Map.of(CommentCreateEditDto.Fields.bookId, "1",
                CommentCreateEditDto.Fields.text, "comment");
        var emptyBookParams = Collections.emptyMap();
        return Stream.of(
                Arguments.of("/books", validBookParams, username, new String[] {}, 403, null),
                Arguments.of("/books", validBookParams, username, new String[] {"TEMP"}, 403, null),
                Arguments.of("/books", validBookParams, username, new String[] {"TEST1"}, 302, "/books"),
                Arguments.of("/books/1/update", validBookParams, username, new String[] {}, 403, null),
                Arguments.of("/books/1/update", validBookParams, username, new String[] {"TEMP"}, 403, null),
                Arguments.of("/books/1/update", validBookParams, username, new String[] {"TEST1"}, 302, "/books"),
                Arguments.of("/books/1/delete", emptyBookParams, username, new String[] {}, 403, null),
                Arguments.of("/books/1/delete", emptyBookParams, username, new String[] {"TEMP"}, 403, null),
                Arguments.of("/books/1/delete", emptyBookParams, username, new String[] {"TEST1"}, 302, "/books"),
                Arguments.of("/comments", validCommentParams, username, new String[] {}, 403, null),
                Arguments.of("/comments", validCommentParams, username, new String[] {"TEMP"}, 403, null),
                Arguments.of("/comments/1/update", validCommentParams, username, new String[] {}, 403, null),
                Arguments.of("/comments/1/update", validCommentParams, username, new String[] {"TEMP"}, 403, null),
                Arguments.of("/comments/1/delete", emptyBookParams, username, new String[] {}, 403, null),
                Arguments.of("/comments/1/delete", emptyBookParams, username, new String[] {"TEMP"}, 403, null)
        );
    }


}