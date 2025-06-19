package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;

@Repository
@RequiredArgsConstructor
public class BookCustomRepositoryImpl implements BookCustomRepository {

    private final MongoTemplate mongoTemplate;

    private final CommentRepository commentRepository;

    @Override
    public void deleteWithComments(String bookId) {
        commentRepository.deleteAllByBookId(bookId);
        Query searchQuery = Query.query(Criteria.where("id").is(bookId));
        mongoTemplate.remove(searchQuery, Book.class);
    }
}
