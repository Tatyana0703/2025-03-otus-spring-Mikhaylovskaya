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
import jakarta.validation.Valid;
import ru.otus.hw.dto.CommentCreateEditDto;
import ru.otus.hw.dto.CommentReadDto;
import ru.otus.hw.services.CommentService;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/api/comments/book/{bookId}")
    public ResponseEntity<List<CommentReadDto>> getAllCommentByBookId(@PathVariable("bookId") long bookId) {
        return ResponseEntity.ok().body(commentService.findAllByBookId(bookId));
    }

    @GetMapping("/api/comments/{id}")
    public ResponseEntity<CommentReadDto> getCommentById(@PathVariable("id") long id) {
        return commentService.findById(id)
                .map(comment -> ResponseEntity.ok().body(comment))
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @PostMapping("/api/comments")
    public ResponseEntity<CommentReadDto> addComment(@Valid @RequestBody CommentCreateEditDto commentDto) {
        CommentReadDto comment = commentService.create(commentDto);
        return ResponseEntity.ok().body(comment);
    }

    @PutMapping("/api/comments/{id}")
    public ResponseEntity<CommentReadDto> updateComment(@PathVariable("id") long id,
                                                  @Valid @RequestBody CommentCreateEditDto commentDto) {
        CommentReadDto comment = commentService.update(id, commentDto);
        return ResponseEntity.ok().body(comment);
    }

    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable("id") long id) {
        commentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
