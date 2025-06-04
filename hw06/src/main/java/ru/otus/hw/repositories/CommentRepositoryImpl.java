package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.Objects.nonNull;
import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public Optional<Comment> findById(long id) {
        EntityGraph<?> entityGraph = em.getEntityGraph("comment-graph");
        Map<String, Object> properties = Map.of(FETCH.getKey(), entityGraph);
        return Optional.ofNullable(em.find(Comment.class, id, properties));
    }

    @Override
    public List<Comment> findByBookId(long bookId) {
        em.find(Book.class, bookId);
        TypedQuery<Comment> query = em.createQuery("select c from Comment c join c.book " +
                "where c.book.id = :id", Comment.class);
        query.setParameter("id", bookId);
        return query.getResultList();
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == 0) {
            em.persist(comment);
            return comment;
        }
        return em.merge(comment);
    }

    @Override
    public void deleteById(long id) {
        Comment comment = em.find(Comment.class, id);
        if (nonNull(comment)) {
            em.remove(comment);
        }
    }
}
