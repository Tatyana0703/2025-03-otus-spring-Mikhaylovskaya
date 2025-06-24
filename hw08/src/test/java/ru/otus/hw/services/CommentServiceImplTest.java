package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.models.Book;
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

    @Autowired
    private BookService bookService;

    @DisplayName("должен загружать список всех комментариев по id книги")
    @Test
    void shouldReturnCorrectCommentsByBookId() {
        String bookId = bookService.findAll().get(0).getId();
        List<Comment> returnedComments = commentService.findByBookId(bookId);
        assertThat(returnedComments).isNotEmpty()
                .hasSizeGreaterThan(1)
                .allMatch(comment -> hasLength(comment.getText()) &&
                        comment.getBook().getId().equals(bookId));
    }

    @DisplayName("должен сохранять новый комментарий")
    @Test
    void shouldInsertNewComment() {
        Book book = bookService.findAll().get(0);
        String commentText = "CommentTest";
        Comment returnedComment = commentService.insert(commentText, book.getId());

        assertThat(returnedComment).isNotNull()
                .matches(comment -> hasLength(comment.getId()) &&
                        comment.getText().equals(commentText) &&
                        comment.getBook().getId().equals(book.getId()));
        assertThat(commentService.findById(returnedComment.getId()))
                .isNotEmpty()
                .get()
                .matches(comment -> comment.getText().equals(commentText) &&
                        comment.getBook().getId().equals(book.getId()));
    }

    @DisplayName("должен сохранять измененный комментарий")
    @Test
    void shouldUpdateComment() {
        Book book = bookService.findAll().get(0);
        Comment updatedComment = commentService.insert("CommentTest", book.getId());
        String newCommentText = "NewCommentTest";

        Comment returnedComment = commentService.update(updatedComment.getId(), newCommentText, book.getId());

        assertThat(returnedComment).isNotNull()
                .matches(comment -> comment.getId().equals(updatedComment.getId()) &&
                        comment.getText().equals(newCommentText) &&
                        comment.getBook().getId().equals(book.getId()));
        assertThat(commentService.findById(returnedComment.getId()))
                .isNotEmpty()
                .get()
                .matches(comment -> comment.getText().equals(newCommentText) &&
                        comment.getBook().getId().equals(book.getId()));
    }

    @DisplayName("должен удалять имеющийся комментарий по id ")
    @Test
    void shouldDeleteComment() {
        String bookId = bookService.findAll().get(0).getId();
        String commentId = commentService.insert("CommentTest", bookId).getId();
        Optional<Comment> deletedComment = commentService.findById(commentId);
        assertThat(deletedComment).isNotEmpty();

        commentService.deleteById(commentId);

        deletedComment = commentService.findById(commentId);
        assertThat(deletedComment).isEmpty();
    }
}