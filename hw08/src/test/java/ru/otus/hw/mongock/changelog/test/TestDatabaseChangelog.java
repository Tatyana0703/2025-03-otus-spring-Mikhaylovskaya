package ru.otus.hw.mongock.changelog.test;

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

import java.util.List;

@ChangeLog
public class TestDatabaseChangelog {

    @ChangeSet(order = "001", id = "dropDb", author = "tmikhaylovskaya", runAlways = true)
    public void dropDb(MongoDatabase db) {
        db.drop();
    }

    @ChangeSet(order = "002", id = "insertData", author = "tmikhaylovskaya")
    public void insertData(BookRepository bookRepository, CommentRepository commentRepository,
                           AuthorRepository authorRepository, GenreRepository genreRepository) {
        List<Author> authors = authorRepository.saveAll(List.of(
                new Author("Test Author 1"), new Author("Test Author 2")));
        List<Genre> genres = genreRepository.saveAll(List.of(
                new Genre("Test Genre 1"), new Genre("Test Genre 2")));
        Book firstBook = bookRepository.save(new Book("Test Title 1", authors.get(0), genres.get(0)));
        Book secondBook = bookRepository.save(new Book("Test Title 2", authors.get(1), genres.get(1)));
        Book thirdBook = bookRepository.save(new Book("Test Title 3", authors.get(1), genres.get(1)));

        commentRepository.save(new Comment("Test Comment 1", firstBook));
        commentRepository.save(new Comment("Test Comment 2", firstBook));
        commentRepository.save(new Comment("Test Comment 3", secondBook));
        commentRepository.save(new Comment("Test Comment 4", secondBook));
        commentRepository.save(new Comment("Test Comment 5", thirdBook));
        commentRepository.save(new Comment("Test Comment 6", thirdBook));
    }
}