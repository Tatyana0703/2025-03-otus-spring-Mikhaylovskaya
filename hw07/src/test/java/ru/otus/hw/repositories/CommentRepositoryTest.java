package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.util.StringUtils.hasLength;

@DisplayName("Репозиторий на основе Jpa для работы с комментариями ")
@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager em;

    private static final long COMMENT_ID = 1L;
    private static final long FIRST_BOOK_ID = 1L;
    private static final int COMMENTS_COUNT_FOR_FIRST_BOOK = 2;
    private static final long UPDATED_COMMENT_ID = 3L;
    private static final long UPDATED_BOOK_ID = 3L;

    @DisplayName("должен загружать комментарий по id")
    @Test
    void shouldReturnCorrectCommentById() {
        Optional<Comment> actualComment = commentRepository.findById(COMMENT_ID);
        Comment expectedComment = em.find(Comment.class, COMMENT_ID);
        assertThat(actualComment).isPresent().get()
                .usingRecursiveComparison().isEqualTo(expectedComment);
    }

    @DisplayName("должен загружать список комментариев по конкретной книге")
    @Test
    void shouldReturnCorrectCommentListByBookId() {
        List<Comment> actualComments = commentRepository.findByBookId(FIRST_BOOK_ID);
        assertThat(actualComments).isNotEmpty()
                .hasSize(COMMENTS_COUNT_FOR_FIRST_BOOK)
                .allMatch(comment -> hasLength(comment.getText()));
    }

    @DisplayName("должен сохранять новый комментарий")
    @Test
    void shouldSaveNewComment() {
        Book book = em.find(Book.class, UPDATED_BOOK_ID);
        assertThat(book).isNotNull();
        Comment expectedComment = Comment.builder()
                .id(0L)
                .text("CommentText_10500")
                .book(book)
                .build();

        var returnedComment = commentRepository.save(expectedComment);

        assertThat(returnedComment).isNotNull()
                .matches(comment -> comment.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedComment);
        em.flush();
        assertThat(em.find(Comment.class, returnedComment.getId()))
                .isNotNull()
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(returnedComment);
    }

    @DisplayName("должен сохранять измененный комментарий")
    @Test
    void shouldSaveUpdatedBook() {
        Book expectedBook = em.find(Book.class, UPDATED_BOOK_ID);
        assertThat(expectedBook).isNotNull();
        var expectedComment = Comment.builder()
                .id(UPDATED_COMMENT_ID)
                .text("CommentText_10500")
                .book(expectedBook)
                .build();
        assertThat(em.find(Comment.class, UPDATED_COMMENT_ID)).isNotNull().isNotEqualTo(expectedComment);

        var returnedComment = commentRepository.save(expectedComment);

        assertThat(returnedComment).isNotNull()
                .matches(comment -> comment.getId() == UPDATED_COMMENT_ID)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedComment);
        em.flush();
        assertThat(em.find(Comment.class, returnedComment.getId()))
                .isNotNull()
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(returnedComment);
    }

    @DisplayName("должен удалять комментарий по id ")
    @Test
    void shouldDeleteComment() {
        Comment comment = em.find(Comment.class, COMMENT_ID);
        assertThat(comment).isNotNull();

        commentRepository.deleteById(COMMENT_ID);

        em.flush();
        comment = em.find(Comment.class, COMMENT_ID);
        assertThat(comment).isNull();
    }
}