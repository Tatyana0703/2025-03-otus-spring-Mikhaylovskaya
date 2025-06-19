package ru.otus.hw.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

@ChangeLog
public class DatabaseChangelog {

    @ChangeSet(order = "001", id = "dropDb", author = "tmikhaylovskaya", runAlways = true)
    public void dropDb(MongoDatabase db) {
        db.drop();
    }

    @ChangeSet(order = "002", id = "insertData", author = "tmikhaylovskaya")
    public void insertData(BookRepository bookRepository, CommentRepository commentRepository) {
        Book book = bookRepository.save(new Book("title_1", "author_1", "genre_1"));
        commentRepository.save(new Comment("comment_1", book));
    }
}