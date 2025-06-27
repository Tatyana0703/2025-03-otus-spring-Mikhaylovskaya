package ru.otus.hw.services;

import ru.otus.hw.models.Book;
import java.util.List;
import java.util.Optional;

public interface BookService {

    Optional<Book> findById(String id);

    List<Book> findAll();

    Book insert(String title, String authorFullName, String genreName);

    Book update(String id, String title, String authorFullName, String genreName);

    void deleteById(String id);
}