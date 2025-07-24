package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.config.SecurityConfiguration;
import ru.otus.hw.dto.UserCreateEditDto;
import ru.otus.hw.services.UserService;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Контроллер для работы с пользователями ")
@WebMvcTest(UserController.class)
@Import({SecurityConfiguration.class})
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private UserService userService;

    @Test
    @WithAnonymousUser
     void shouldAllowRegistrationForUnauthenticatedUser() throws Exception {
        mvc.perform(post("/users")
                        .param(UserCreateEditDto.Fields.username, "test-user")
                        .param(UserCreateEditDto.Fields.rawPassword, "qwerty")
                )
                .andExpect(view().name("redirect:/login"));
        verify(userService, times(1))
                .create(new UserCreateEditDto("test-user", "qwerty"));
    }

    @Test
    @WithAnonymousUser
    void shouldAllowLoginForUnauthenticatedUser() throws Exception {
        mvc.perform(get("/login")).andExpect(view().name("user/login"));
    }

    @Test
    @WithAnonymousUser
    void shouldAccessToRegistrationPageForUnauthenticatedUser() throws Exception {
        mvc.perform(get("/users/registration")).andExpect(view().name("/user/registration"));
    }

    @WithAnonymousUser
    @ParameterizedTest
    @CsvSource({"/books", "/books/registration", "/books/1", "/authors", "/genres", "/comments/book/1"})
    void shouldNotGetAccessForUnauthenticatedUser(String urlTemplate) throws Exception {
        mvc.perform(get(urlTemplate))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("http://*/login"));
    }

    @WithAnonymousUser
    @ParameterizedTest
    @CsvSource({"/books", "/books/1/update", "/books/1/delete"})
    void shouldNotPostAccessForUnauthenticatedUser(String urlTemplate) throws Exception {
        mvc.perform(post(urlTemplate))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("http://*/login"));
    }
}