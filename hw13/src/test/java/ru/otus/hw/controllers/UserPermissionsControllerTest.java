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
import ru.otus.hw.services.RoleService;
import ru.otus.hw.services.UserService;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import static java.util.Objects.nonNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Контроллер для работы с пользователями ")
@WebMvcTest(UserController.class)
@Import(SecurityConfiguration.class)
class UserPermissionsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private RoleService roleService;

    @ParameterizedTest(name = "{0} {1} for user {2} should return {4} status")
    @MethodSource("getTestData")
    void shouldReturnExpectedStatus(String method, String url,
                                    String userName, String[] roles,
                                    int status, boolean checkLoginRedirection) throws Exception {
        var request = methodRequestBuilder(method, url);
        if (nonNull(userName)) {
            request = request.with(user(userName).roles(roles));
        }
        ResultActions resultActions = mockMvc.perform(request)
                .andExpect(status().is(status));
        if (checkLoginRedirection) {
            resultActions.andExpect(redirectedUrlPattern("**/login"));
        }
    }

    private static Stream<Arguments> getTestData() {
        return Stream.of(
                Arguments.of("get", "/books", null, null, 302, true),
                Arguments.of("get", "/books/registration", null, null, 302, true),
                Arguments.of("get", "/books/1", null, null, 302, true),
                Arguments.of("get", "/authors", null, null, 302, true),
                Arguments.of("get", "/genres", null, null, 302, true),
                Arguments.of("get", "/comments", null, null, 302, true),
                Arguments.of("get", "/comments/1", null, null, 302, true),
                Arguments.of("get", "/comments/registration", null, null, 302, true),
                Arguments.of("post", "/books", null, null, 302, true),
                Arguments.of("post", "/books/1/update", null, null, 302, true),
                Arguments.of("post", "/books/1/delete", null, null, 302, true),
                Arguments.of("post", "/comments", null, null, 302, true),
                Arguments.of("post", "/comments/1/update", null, null, 302, true),
                Arguments.of("post", "/comments/1/delete", null, null, 302, true),
                Arguments.of("get", "/login", null, null, 200, false),
                Arguments.of("get", "/users/registration", null, null, 200, false)
        );
    }

    private MockHttpServletRequestBuilder methodRequestBuilder(String method, String url) {
        Map<String, Function<String, MockHttpServletRequestBuilder>> methodMap = Map.of(
                "get", MockMvcRequestBuilders::get,
                "post", MockMvcRequestBuilders::post);
        return methodMap.get(method).apply(url);
    }
}