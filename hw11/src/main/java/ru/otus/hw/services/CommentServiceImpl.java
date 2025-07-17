package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentCreateEditDto;
import ru.otus.hw.dto.CommentReadDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.mappers.CommentCreateEditMapper;
import ru.otus.hw.mappers.CommentReadMapper;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    private final CommentReadMapper commentReadMapper;

    private final CommentCreateEditMapper commentCreateEditMapper;

    @Override
    public Mono<CommentReadDto> findById(Long id) {
        return commentRepository.findById(id).map(commentReadMapper::map);
    }

    @Override
    public Flux<CommentReadDto> findByBookId(Long bookId) {
        return commentRepository.findAllByBookId(bookId)
                .switchIfEmpty(Mono.error(new NotFoundException("Book with id %d not found".formatted(bookId))))
                .map(commentReadMapper::map);
    }

    @Override
    @Transactional
    public Mono<CommentReadDto> create(CommentCreateEditDto commentDto) {
        Comment comment = commentCreateEditMapper.map(commentDto);
        Mono<Book> bookMono = bookRepository.findById(commentDto.getBookId())
                .switchIfEmpty(Mono.error(
                        new NotFoundException("Book with id %d not found".formatted(comment.getBookId()))));
        return bookMono.flatMap(value ->
                commentRepository.save(comment).flatMap(savedComment ->
                        Mono.just(commentReadMapper.map(savedComment)))
        );
    }

    @Override
    @Transactional
    public Mono<CommentReadDto> update(Long id, CommentCreateEditDto commentDto) {
        Comment comment = commentCreateEditMapper.map(commentDto);
        Mono<Comment> commentMono = commentRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new NotFoundException("Comment with id %d not found".formatted(comment.getId()))));
        Mono<Book> bookMono = bookRepository.findById(commentDto.getBookId())
                .switchIfEmpty(Mono.error(
                        new NotFoundException("Book with id %d not found".formatted(comment.getBookId()))));
        return Mono.zip(commentMono, bookMono).flatMap(value -> {
            value.getT1().setText(commentDto.getText());
            value.getT1().setBookId(commentDto.getBookId());
            return commentRepository.save(value.getT1()).flatMap(savedComment ->
                    Mono.just(commentReadMapper.map(savedComment)));
        });
    }

    @Override
    @Transactional
    public Mono<Void> deleteById(Long id) {
        return commentRepository.deleteById(id);
    }
}
