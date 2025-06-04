package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentExtendRepositoryImpl implements CommentExtendRepository {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public List<Comment> findByBookId(long bookId) {
        em.find(Book.class, bookId);
        TypedQuery<Comment> query = em.createQuery("select c from Comment c join c.book " +
                "where c.book.id = :id", Comment.class);
        query.setParameter("id", bookId);
        return query.getResultList();
    }
}
