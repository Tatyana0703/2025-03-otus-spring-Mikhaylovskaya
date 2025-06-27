package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final CommentRepository commentRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> findById(String id) {
        return bookRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    @Transactional
    public Book insert(String title, String authorFullName, String genreName) {
        Author author = authorRepository.findByFullName(authorFullName)
                .orElseThrow(() -> new EntityNotFoundException("Author '%s' not found".formatted(authorFullName)));
        Genre genre = genreRepository.findByName(genreName)
                .orElseThrow(() -> new EntityNotFoundException("Genre '%s' not found".formatted(genreName)));
        Book book = new Book(title, author, genre);
        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public Book update(String id, String title, String authorFullName, String genreName) {
        Book updatedBook = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(id)));
        Author author = authorRepository.findByFullName(authorFullName)
                .orElseThrow(() -> new EntityNotFoundException("Author '%s' not found".formatted(authorFullName)));
        Genre genre = genreRepository.findByName(genreName)
                .orElseThrow(() -> new EntityNotFoundException("Genre '%s' not found".formatted(genreName)));
        updatedBook.setTitle(title);
        updatedBook.setAuthor(author);
        updatedBook.setGenre(genre);
        return bookRepository.save(updatedBook);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        commentRepository.deleteAllByBookId(id);
        bookRepository.deleteById(id);
    }
}