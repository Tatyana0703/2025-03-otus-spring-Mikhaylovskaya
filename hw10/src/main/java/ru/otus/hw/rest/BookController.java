package ru.otus.hw.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.otus.hw.dto.BookReadDto;
import ru.otus.hw.services.BookService;
import ru.otus.hw.dto.BookCreateEditDto;
import jakarta.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class BookController {

    private final BookService bookService;

    @GetMapping("/api/books")
    public ResponseEntity<List<BookReadDto>> getAllBooks() {
        return ResponseEntity.ok().body(bookService.findAll());
    }

    @GetMapping("/api/books/{id}")
    public ResponseEntity<BookReadDto> getBookById(@PathVariable("id") long id) {
        return bookService.findById(id)
                .map(book -> ResponseEntity.ok().body(book))
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @PostMapping("/api/books")
    public ResponseEntity<BookReadDto> addBook(@Valid @RequestBody BookCreateEditDto bookDto) {
        BookReadDto book = bookService.create(bookDto);
        return ResponseEntity.ok().body(book);
    }

    @PutMapping("/api/books/{id}")
    public ResponseEntity<BookReadDto> updateBook(@PathVariable("id") long id,
                                                  @Valid @RequestBody BookCreateEditDto bookDto) {
        BookReadDto book = bookService.update(id, bookDto);
        return ResponseEntity.ok().body(book);
    }

    @DeleteMapping("/api/books/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable("id") long id) {
        bookService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
