package ru.otus.hw.mongock.changelog.test;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

@ChangeLog
public class TestDatabaseChangelog {

    @ChangeSet(order = "001", id = "dropDb", author = "tmikhaylovskaya", runAlways = true)
    public void dropDb(MongoDatabase db) {
        db.drop();
    }

    @ChangeSet(order = "002", id = "insertData", author = "tmikhaylovskaya")
    public void insertData(BookRepository bookRepository, CommentRepository commentRepository) {
        Book firstBook = bookRepository.save(new Book("title_1", "author_1", "genre_1"));
        Book secondBook = bookRepository.save(new Book("title_2", "author_2", "genre_2"));
        Book thirdBook = bookRepository.save(new Book("title_3", "author_2", "genre_2"));

        commentRepository.save(new Comment("comment_1", firstBook));
        commentRepository.save(new Comment("comment_2", firstBook));
        commentRepository.save(new Comment("comment_3", secondBook));
        commentRepository.save(new Comment("comment_4", secondBook));
        commentRepository.save(new Comment("comment_5", thirdBook));
        commentRepository.save(new Comment("comment_6", thirdBook));
    }
}