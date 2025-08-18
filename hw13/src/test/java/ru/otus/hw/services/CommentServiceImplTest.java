package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.jdbc.Sql;
import ru.otus.hw.dto.BookReadDto;
import ru.otus.hw.dto.CommentCreateEditDto;
import ru.otus.hw.dto.CommentReadDto;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.security.SecurityUserDetails;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.util.StringUtils.hasLength;

@DisplayName("Сервис для работы с комментариями ")
@SpringBootTest
class CommentServiceImplTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private BookService bookService;

    @Autowired
    private CommentRepository commentRepository;

    @DisplayName("должен загружать комментарий по id")
    @Test
    void shouldReturnCorrectCommentById() {
        long bookId = bookService.findAll().get(0).getId();
        long commentId = commentService.findAllByBookId(bookId).get(0).getId();
        Optional<CommentReadDto> actualComment = commentService.findById(commentId);
        assertThat(actualComment).isNotEmpty().get()
                .matches(comment -> hasLength(comment.getText()));
    }

    @DisplayName("должен загружать список всех комментариев по id книги")
    @Test
    void shouldReturnCorrectCommentsListByBookId() {
        long bookId = bookService.findAll().get(0).getId();
        int expectedSize= 4;
        List<CommentReadDto> actualComments = commentService.findAllByBookId(bookId);
        assertThat(actualComments).isNotEmpty()
                .hasSize(expectedSize)
                .allMatch(comment -> hasLength(comment.getText()));
    }

    @Sql(scripts = "classpath:test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("должен сохранять новый комментарий")
    @Test
    void shouldInsertNewComment() {
        var username = "username_1";
        UserDetails userDetails = new SecurityUserDetails(username, null, Collections.emptyList());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        long bookId = bookService.findAll().get(0).getId();
        CommentCreateEditDto commentDto = CommentCreateEditDto.builder().text("Comment text").bookId(bookId).build();
        CommentReadDto returnedComment = commentService.create(commentDto, userDetails);
        assertThat(returnedComment).isNotNull()
                .matches(comment -> comment.getId() > 0 &&
                        comment.getText().equals(commentDto.getText()) &&
                        comment.getBookId() == bookId);
        assertThat(commentService.findById(returnedComment.getId()))
                .isNotEmpty().get()
                .matches(comment -> comment.getText().equals(commentDto.getText()) &&
                        comment.getBookId() == bookId);
    }

    @Sql(scripts = "classpath:test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("должен сохранять измененный комментарий")
    @Test
    void shouldUpdateBook() {
        var username = "username_1";
        UserDetails userDetails = new SecurityUserDetails(username, null, Collections.emptyList());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<BookReadDto> allBooks = bookService.findAll();
        long bookId = allBooks.get(0).getId();
        long commentId = commentService.findAllByBookId(bookId).get(0).getId();
        CommentCreateEditDto commentDto = CommentCreateEditDto.builder().text("Comment text updated").bookId(bookId).build();
        Optional<CommentReadDto> updatedComment = commentService.findById(commentId);
        assertThat(updatedComment)
                .isNotEmpty().get()
                .matches(comment -> !comment.getText().equals(commentDto.getText()));

        CommentReadDto returnedComment = commentService.update(commentId, commentDto, userDetails);

        assertThat(returnedComment).isNotNull()
                .matches(comment -> comment.getId() == commentId &&
                        comment.getText().equals(commentDto.getText()) &&
                        comment.getBookId() == bookId);
        assertThat(commentService.findById(returnedComment.getId()))
                .isNotEmpty().get()
                .matches(comment -> comment.getText().equals(commentDto.getText()) &&
                        comment.getBookId() == bookId);
    }

    @Sql(scripts = "classpath:test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("должен удалять имеющийся комментарий по id ")
    @Test
    void deleteById() {
        long bookId = bookService.findAll().get(0).getId();
        long commentId = commentService.findAllByBookId(bookId).get(0).getId();
        Optional<CommentReadDto> deletedComment = commentService.findById(commentId);
        assertThat(deletedComment).isNotEmpty();

        commentService.deleteById(commentId);

        deletedComment = commentService.findById(commentId);
        assertThat(deletedComment).isEmpty();
    }

    @DisplayName("должен возвращать имя пользователя являющегося владельцем комментария ")
    @Test
    void shouldReturnUsernameOfCommentOwner() {
        long bookId = bookService.findAll().get(0).getId();
        CommentReadDto commentReadDto = commentService.findAllByBookId(bookId).get(0);
        String expectedUsername = commentRepository.findById(commentReadDto.getId()).get().getUser().getUsername();
        String returnUsername = commentService.getCommentAuthorNameById(commentReadDto.getId());
        assertThat(returnUsername).isEqualTo(expectedUsername);
    }
}