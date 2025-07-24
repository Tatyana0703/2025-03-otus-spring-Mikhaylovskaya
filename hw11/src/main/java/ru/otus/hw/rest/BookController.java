package ru.otus.hw.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookReadDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.services.BookService;
import ru.otus.hw.dto.BookCreateEditDto;
import jakarta.validation.Valid;

@RequiredArgsConstructor
@RestController
public class BookController {

    private final BookService bookService;

    @GetMapping("/api/books")
    public Flux<BookReadDto> getAllBooks() {
        return bookService.findAll();
    }

    @GetMapping("/api/books/{id}")
    public Mono<ResponseEntity<BookReadDto>> getBookById(@PathVariable("id") long id) {
        return bookService.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Book with id %d not found".formatted(id))))
                .map(book -> ResponseEntity.ok().body(book));
    }

    @PostMapping("/api/books")
    public Mono<ResponseEntity<BookReadDto>> addBook(@Valid @RequestBody BookCreateEditDto bookDto) {
        return bookService.create(bookDto).map(book -> ResponseEntity.status(HttpStatus.CREATED).body(book));
    }

    @PutMapping("/api/books/{id}")
    public Mono<ResponseEntity<BookReadDto>> updateBook(@PathVariable("id") long id,
                                                  @Valid @RequestBody BookCreateEditDto bookDto) {
        return bookService.update(id, bookDto)
                .switchIfEmpty(Mono.error(new NotFoundException("Book with id %d not found".formatted(id))))
                .map(book -> ResponseEntity.ok().body(book));
    }

    @DeleteMapping("/api/books/{id}")
    public Mono<ResponseEntity<Void>> deleteBook(@PathVariable("id") long id) {
        return bookService.deleteById(id).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
    }
}
