package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.models.Comment;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.util.StringUtils.hasLength;

@DisplayName("Сервис для работы с комментариями ")
@SpringBootTest
class CommentServiceImplTest {

    @Autowired
    private CommentService commentService;

    private static final long SEARCHED_COMMENT_ID = 1L;
    private static final long SEARCHED_BOOK_ID = 1L;
    private static final long DELETED_COMMENT_ID = 3L;
    private static final long UPDATED_COMMENT_ID = 4L;
    private static final String UPDATED_COMMENT_TEXT = "Text updated";
    private static final long UPDATED_COMMENT_FOR_BOOK_ID = 3L;
    private static final long INSERTED_COMMENT_FOR_BOOK_ID = 2L;

    @DisplayName("должен загружать комментарий по id")
    @Test
    void shouldReturnCorrectCommentById() {
        Optional<Comment> actualComment = commentService.findById(SEARCHED_COMMENT_ID);
        assertThat(actualComment).isNotEmpty().get()
                .matches(comment ->
                                hasLength(comment.getText()) &&
                                comment.getBook().getId() == SEARCHED_BOOK_ID &&
                                hasLength(comment.getBook().getTitle())
                        );
    }

    @DisplayName("должен загружать список всех комментариев по id книги")
    @Test
    void shouldReturnCorrectCommentsListByBookId() {
        List<Comment> actualComments = commentService.findByBookId(SEARCHED_BOOK_ID);
        assertThat(actualComments).isNotEmpty()
                .hasSizeGreaterThan(1)
                .allMatch(
                        comment -> hasLength(comment.getText()) &&
                                comment.getBook().getId() == SEARCHED_BOOK_ID &&
                                hasLength(comment.getBook().getTitle())
                );
    }

    @DisplayName("должен сохранять новый комментарий")
    @Test
    void shouldInsertNewComment() {
        var commentText = "Comment_text";
        var returnedComment = commentService.insert(commentText, INSERTED_COMMENT_FOR_BOOK_ID);

        assertThat(returnedComment).isNotNull()
                .matches(
                        comment -> comment.getId() > 0 &&
                                comment.getText().equals(commentText) &&
                                comment.getBook().getId() == INSERTED_COMMENT_FOR_BOOK_ID
                );
        assertThat(commentService.findById(returnedComment.getId()))
                .isNotEmpty()
                .get()
                .matches(
                        comment -> comment.getText().equals(commentText) &&
                                comment.getBook().getId() == INSERTED_COMMENT_FOR_BOOK_ID
                );
    }

    @DisplayName("должен сохранять измененный комментарий")
    @Test
    void shouldUpdateBook() {
        var updatedComment = commentService.findById(UPDATED_COMMENT_ID);
        assertThat(updatedComment)
                .isNotEmpty()
                .get()
                .matches(comment -> !comment.getText().equals(UPDATED_COMMENT_TEXT));

        var returnedComment = commentService.update(UPDATED_COMMENT_ID, UPDATED_COMMENT_TEXT, UPDATED_COMMENT_FOR_BOOK_ID);

        assertThat(returnedComment).isNotNull()
                .matches(
                        comment -> comment.getId() == UPDATED_COMMENT_ID &&
                                comment.getText().equals(UPDATED_COMMENT_TEXT) &&
                                comment.getBook().getId() == UPDATED_COMMENT_FOR_BOOK_ID
                );
        assertThat(commentService.findById(returnedComment.getId()))
                .isNotEmpty()
                .get()
                .matches(
                        comment -> comment.getText().equals(UPDATED_COMMENT_TEXT) &&
                                comment.getBook().getId() == UPDATED_COMMENT_FOR_BOOK_ID
                );
    }

    @DisplayName("должен удалять имеющийся комментарий по id ")
    @Test
    void deleteById() {
        Optional<Comment> deletedComment = commentService.findById(DELETED_COMMENT_ID);
        assertThat(deletedComment).isNotEmpty();

        commentService.deleteById(DELETED_COMMENT_ID);

        deletedComment = commentService.findById(DELETED_COMMENT_ID);
        assertThat(deletedComment).isEmpty();
    }
}