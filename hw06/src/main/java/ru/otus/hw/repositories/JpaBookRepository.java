package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.Objects.isNull;
import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

@Repository
public class JpaBookRepository implements BookRepository {

    @PersistenceContext
    private final EntityManager em;

    private final CommentRepository commentRepository;

    public JpaBookRepository(EntityManager em, CommentRepository commentRepository) {
        this.em = em;
        this.commentRepository = commentRepository;
    }

    @Override
    public Optional<Book> findById(long id) {
        EntityGraph<?> entityGraph = em.getEntityGraph("author-genre-graph");
        Map<String, Object> properties = Map.of(FETCH.getKey(), entityGraph);
        Book book = em.find(Book.class, id, properties);
        return Optional.ofNullable(book);
    }

    @Override
    public List<Book> findAll() {
        EntityGraph<?> entityGraph = em.getEntityGraph("author-genre-graph");
        TypedQuery<Book> query = em.createQuery("select b from Book b", Book.class);
        query.setHint(FETCH.getKey(), entityGraph);
        return query.getResultList();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            em.persist(book);
            return book;
        }
        if (isNull(em.find(Book.class, book.getId()))) {
            throw new EntityNotFoundException("Book with id = %d not updated".formatted(book.getId()));
        }
        return em.merge(book);
    }

    @Override
    public void deleteById(long id) {
        Book book = em.find(Book.class, id);
        if (isNull(book)) {
            throw new EntityNotFoundException("Book with id = %d not deleted".formatted(id));
        }
        commentRepository.findByBookId(id).forEach(em::remove);
        em.remove(book);
    }
}