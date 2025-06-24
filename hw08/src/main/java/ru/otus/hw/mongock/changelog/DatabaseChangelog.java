package ru.otus.hw.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

@ChangeLog
public class DatabaseChangelog {

    @ChangeSet(order = "001", id = "dropDb", author = "tmikhaylovskaya", runAlways = true)
    public void dropDb(MongoDatabase db) {
        db.drop();
    }

    @ChangeSet(order = "002", id = "insertData", author = "tmikhaylovskaya")
    public void insertData(BookRepository bookRepository, CommentRepository commentRepository,
                           AuthorRepository authorRepository, GenreRepository genreRepository) {
        Author author = authorRepository.save(new Author("Author 1"));
        Genre genre = genreRepository.save(new Genre("Genre 1"));
        Book book = bookRepository.save(new Book("Title 1", author, genre));
        commentRepository.save(new Comment("Comment 1", book));
    }
}