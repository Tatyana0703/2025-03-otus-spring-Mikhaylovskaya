package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.otus.hw.dto.BookReadDto;
import ru.otus.hw.dto.CommentCreateEditDto;
import ru.otus.hw.dto.CommentReadDto;
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
        int expectedSize= 2;
        List<CommentReadDto> actualComments = commentService.findAllByBookId(bookId);
        assertThat(actualComments).isNotEmpty()
                .hasSize(expectedSize)
                .allMatch(comment -> hasLength(comment.getText()));
    }

    @Sql(scripts = "classpath:test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("должен сохранять новый комментарий")
    @Test
    void shouldInsertNewComment() {
        long bookId = bookService.findAll().get(0).getId();
        CommentCreateEditDto commentDto = new CommentCreateEditDto("Comment text", bookId);
        CommentReadDto returnedComment = commentService.create(commentDto);
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
        List<BookReadDto> allBooks = bookService.findAll();
        long bookId = allBooks.get(0).getId();
        long updatedBookId = allBooks.get(1).getId();
        long commentId = commentService.findAllByBookId(bookId).get(0).getId();
        CommentCreateEditDto commentDto = new CommentCreateEditDto("Comment text updated", updatedBookId);
        Optional<CommentReadDto> updatedComment = commentService.findById(commentId);
        assertThat(updatedComment)
                .isNotEmpty().get()
                .matches(comment -> !comment.getText().equals(commentDto.getText()));

        CommentReadDto returnedComment = commentService.update(commentId, commentDto);

        assertThat(returnedComment).isNotNull()
                .matches(comment -> comment.getId() == commentId &&
                        comment.getText().equals(commentDto.getText()) &&
                        comment.getBookId() == updatedBookId);
        assertThat(commentService.findById(returnedComment.getId()))
                .isNotEmpty().get()
                .matches(comment -> comment.getText().equals(commentDto.getText()) &&
                        comment.getBookId() == updatedBookId);
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
}