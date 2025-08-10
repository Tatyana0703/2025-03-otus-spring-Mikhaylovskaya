package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentCreateEditDto;
import ru.otus.hw.dto.CommentReadDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.mappers.CommentCreateEditMapper;
import ru.otus.hw.mappers.CommentReadMapper;
import ru.otus.hw.models.Comment;
import java.util.List;
import java.util.Optional;
import ru.otus.hw.repositories.UserRepository;
import ru.otus.hw.models.User;
import ru.otus.hw.repositories.CommentRepository;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final CommentReadMapper commentReadMapper;

    private final CommentCreateEditMapper commentCreateEditMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<CommentReadDto> findById(long id) {
        return commentRepository.findById(id).map(commentReadMapper::map);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentReadDto> findAllByBookId(long bookId) {
        return commentRepository.findAllByBookId(bookId).stream()
                .map(commentReadMapper::map)
                .toList();
    }

    @Override
    @Transactional
    public CommentReadDto create(CommentCreateEditDto commentDto, UserDetails userDetails) {
        Comment comment = commentCreateEditMapper.map(commentDto);
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("User with username '%s' not found"
                        .formatted(userDetails.getUsername())));
        comment.setUser(user);
        commentRepository.save(comment);
        return commentReadMapper.map(comment);
    }

    @Override
    @Transactional
    public CommentReadDto update(long id, CommentCreateEditDto commentDto, UserDetails userDetails) {
        Comment updatedComment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment with id %d not found".formatted(id)));
        updatedComment = commentCreateEditMapper.map(commentDto, updatedComment);
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("User with username '%s' not found"
                        .formatted(userDetails.getUsername())));
        updatedComment.setUser(user);
        commentRepository.save(updatedComment);
        return commentReadMapper.map(updatedComment);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkCommentOwner(long id, UserDetails userDetails) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment with id %d not found".formatted(id)));
        return comment.getUser().getUsername().equals(userDetails.getUsername());
    }
}
