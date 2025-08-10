package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.config.SecurityConfiguration;
import ru.otus.hw.dto.UserCreateEditDto;
import ru.otus.hw.services.RoleService;
import ru.otus.hw.services.UserService;
import java.util.List;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@DisplayName("Контроллер для работы с пользователями ")
@WebMvcTest(UserController.class)
@Import(SecurityConfiguration.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private RoleService roleService;

    @Test
    void shouldAllowRegistrationForUnauthenticatedUser() throws Exception {
        mockMvc.perform(post("/users")
                        .param(UserCreateEditDto.Fields.username, "test-user")
                        .param(UserCreateEditDto.Fields.rawPassword, "qwerty")
                        .param(UserCreateEditDto.Fields.roleIds, String.valueOf(1))
                )
                .andExpect(view().name("redirect:/login"));
        verify(userService, times(1))
                .create(new UserCreateEditDto("test-user", "qwerty", List.of(1)));
    }

    @Test
    void shouldAllowLoginForUnauthenticatedUser() throws Exception {
        mockMvc.perform(get("/login")).andExpect(view().name("user/login"));
    }

    @Test
    void shouldAccessToRegistrationPageForUnauthenticatedUser() throws Exception {
        mockMvc.perform(get("/users/registration")).andExpect(view().name("/user/registration"));
    }
}
