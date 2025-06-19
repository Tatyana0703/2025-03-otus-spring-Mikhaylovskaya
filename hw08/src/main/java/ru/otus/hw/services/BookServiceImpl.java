package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.Author;
import ru.otus.hw.dto.Genre;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.BookRepository;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

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
    public Book insert(String title, String author, String genre) {
        Book book = new Book(title, author, genre);
        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public Book update(String id, String title, String author, String genre) {
        Book updatedBook = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(id)));
        updatedBook.setTitle(title);
        updatedBook.setAuthor(author);
        updatedBook.setGenre(genre);
        return bookRepository.save(updatedBook);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        bookRepository.deleteWithComments(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Author> findAuthors() {
        return bookRepository.findAuthors();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Genre> findGenres() {
        return bookRepository.findGenres();
    }
}