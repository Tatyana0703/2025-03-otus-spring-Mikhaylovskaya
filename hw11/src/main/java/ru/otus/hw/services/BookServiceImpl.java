package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookCreateEditDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.mappers.BookCreateEditMapper;
import ru.otus.hw.mappers.BookReadMapper;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.dto.BookReadDto;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookReadMapper bookReadMapper;

    private final BookCreateEditMapper bookCreateEditMapper;

    @Override
    public Mono<BookReadDto> findById(Long id) {
        return bookRepository.findByIdCustom(id).map(bookReadMapper::map);
    }

    @Override
    public Flux<BookReadDto> findAll() {
        return bookRepository.findAllCustom().map(bookReadMapper::map);
    }

    @Override
    @Transactional
    public Mono<BookReadDto> create(BookCreateEditDto bookDto) {
        Book book = bookCreateEditMapper.map(bookDto);
        Mono<Author> authorMono = authorRepository.findById(book.getAuthorId())
                .switchIfEmpty(Mono.error(
                        new NotFoundException("Author with id %d not found".formatted(book.getAuthorId()))));
        Mono<Genre> genreMono = genreRepository.findById(book.getGenreId())
                .switchIfEmpty(Mono.error(
                        new NotFoundException("Genre with id %d not found".formatted(book.getGenreId()))));
        return Mono.zip(authorMono, genreMono)
                .flatMap(value -> bookRepository.save(book)
                        .map(savedBook -> bookReadMapper.map(savedBook, value.getT1(), value.getT2())));
    }

    @Override
    @Transactional
    public Mono<BookReadDto> update(Long id, BookCreateEditDto bookDto) {
        Mono<Book> bookMono = bookRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Book with id %d not found".formatted(id))));
        Mono<Author> authorMono = authorRepository.findById(bookDto.getAuthorId())
                .switchIfEmpty(Mono.error(
                        new NotFoundException("Author with id %d not found".formatted(bookDto.getAuthorId()))));
        Mono<Genre> genreMono = genreRepository.findById(bookDto.getGenreId())
                .switchIfEmpty(Mono.error(
                        new NotFoundException("Genre with id %d not found".formatted(bookDto.getGenreId()))));
        return Mono.zip(bookMono, authorMono, genreMono).flatMap(value -> {
                value.getT1().setTitle(bookDto.getTitle());
                value.getT1().setAuthorId(value.getT2().getId());
                value.getT1().setGenreId(value.getT3().getId());
                return bookRepository.save(value.getT1())
                        .map(savedBook -> bookReadMapper.map(savedBook, value.getT2(), value.getT3()));
        });
    }

    @Override
    @Transactional
    public Mono<Void> deleteById(Long id) {
        return bookRepository.deleteById(id);
    }
}