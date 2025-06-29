package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentCreateEditDto;
import ru.otus.hw.dto.CommentReadDto;
import ru.otus.hw.mappers.CommentCreateEditMapper;
import ru.otus.hw.mappers.CommentReadMapper;
import ru.otus.hw.models.Comment;
import java.util.List;
import java.util.Optional;
import ru.otus.hw.repositories.CommentRepository;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final CommentReadMapper commentReadMapper;

    private final CommentCreateEditMapper commentCreateEditMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<CommentReadDto> findById(long id) {
        return commentRepository.findById(id).map(commentReadMapper::map);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentReadDto> findByBookId(long bookId) {
        return commentRepository.findAllByBookId(bookId).stream()
                .map(commentReadMapper::map)
                .toList();
    }

    @Override
    @Transactional
    public CommentReadDto create(CommentCreateEditDto commentDto) {
        Comment comment = commentCreateEditMapper.map(commentDto);
        commentRepository.save(comment);
        return commentReadMapper.map(comment);
    }

    @Override
    @Transactional
    public Optional<CommentReadDto> update(long id, CommentCreateEditDto commentDto) {
        return commentRepository.findById(id)
                .map(entity -> commentCreateEditMapper.map(commentDto, entity))
                .map(commentRepository::save)
                .map(commentReadMapper::map);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }
}
