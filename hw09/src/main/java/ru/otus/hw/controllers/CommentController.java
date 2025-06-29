package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.otus.hw.services.CommentService;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/comments/book/{id}")
    public String findAllByBookId(@PathVariable("id") long bookId, Model model) {
        model.addAttribute("comments", commentService.findByBookId(bookId));
        return "comment/list";
    }
}
