package ru.otus.hw.rest;

import jakarta.validation.Valid;
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
import ru.otus.hw.dto.CommentCreateEditDto;
import ru.otus.hw.dto.CommentReadDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.services.CommentService;

@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/api/comments/{id}")
    public Mono<ResponseEntity<CommentReadDto>> getCommentById(@PathVariable("id") Long id) {
        return commentService.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Comment with id %d not found".formatted(id))))
                .map(comment -> ResponseEntity.ok().body(comment));
    }

    @GetMapping("/api/comments/book/{id}")
    public Flux<CommentReadDto> getAllCommentsByBookId(@PathVariable("id") Long bookId) {
        return commentService.findByBookId(bookId);
    }

    @PostMapping("/api/comments")
    public Mono<ResponseEntity<CommentReadDto>> addBook(@Valid @RequestBody CommentCreateEditDto commentDto) {
        return commentService.create(commentDto).map(comment -> ResponseEntity.status(HttpStatus.CREATED).body(comment));
    }

    @PutMapping("/api/comments/{id}")
    public Mono<ResponseEntity<CommentReadDto>> updateBook(@PathVariable("id") Long id,
                                                        @Valid @RequestBody CommentCreateEditDto commentDto) {
        return commentService.update(id, commentDto)
                .switchIfEmpty(Mono.error(new NotFoundException("Comment with id %d not found".formatted(id))))
                .map(comment -> ResponseEntity.ok().body(comment));
    }

    @DeleteMapping("/api/comments/{id}")
    public Mono<ResponseEntity<Void>> deleteComment(@PathVariable("id") Long id) {
        return commentService.deleteById(id).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
    }
}
