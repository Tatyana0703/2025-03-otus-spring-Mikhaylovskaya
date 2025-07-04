package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookCreateEditDto;
import ru.otus.hw.mappers.BookCreateEditMapper;
import ru.otus.hw.mappers.BookReadMapper;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.BookRepository;
import java.util.List;
import java.util.Optional;
import ru.otus.hw.dto.BookReadDto;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final BookReadMapper bookReadMapper;

    private final BookCreateEditMapper bookCreateEditMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<BookReadDto> findById(long id) {
        return bookRepository.findById(id).map(bookReadMapper::map);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookReadDto> findAll() {
        return bookRepository.findAll().stream()
                .map(bookReadMapper::map)
                .toList();
    }

    @Override
    @Transactional
    public BookReadDto create(BookCreateEditDto bookDto) {
        Book book = bookCreateEditMapper.map(bookDto);
        bookRepository.save(book);
        return bookReadMapper.map(book);
    }

    @Override
    @Transactional
    public Optional<BookReadDto> update(long id, BookCreateEditDto bookDto) {
        return bookRepository.findById(id)
                .map(entity -> bookCreateEditMapper.map(bookDto, entity))
                .map(bookRepository::save)
                .map(bookReadMapper::map);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        bookRepository.deleteById(id);
    }
}