package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.otus.hw.dto.CommentCreateEditDto;
import ru.otus.hw.dto.CommentReadDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.services.CommentService;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping(path = "/comments", params = "bookId")
    public String findAllByBookId(@RequestParam("bookId") long bookId, Model model) {
        model.addAttribute("comments", commentService.findAllByBookId(bookId));
        model.addAttribute("bookId", bookId);
        return "comment/list";
    }

    @GetMapping("/comments/{id}")
    public String findById(@PathVariable("id") long id, Model model) {
        CommentReadDto comment = commentService.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment with id %d not found".formatted(id)));
        model.addAttribute("comment", comment);
        return "comment/edit";
    }

    @GetMapping(path = "/comments/registration", params = "bookId")
    public String registration(@RequestParam("bookId") long bookId, Model model) {
        var commentDto = CommentCreateEditDto.builder().bookId(bookId).build();
        model.addAttribute("comment", commentDto);
        return "comment/registration";
    }

    @PostMapping("/comments")
    public String create(@ModelAttribute @Valid CommentCreateEditDto commentDto, BindingResult bindingResult,
                         RedirectAttributes redirectAttributes, @AuthenticationPrincipal UserDetails userDetails) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("comment", commentDto);
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/comments/registration?bookId=" + commentDto.getBookId();
        }
        CommentReadDto comment = commentService.create(commentDto, userDetails);
        return "redirect:/comments?bookId=" + comment.getBookId();
    }

    @PreAuthorize("@commentServiceImpl.checkCommentOwner(#id, principal)")
    @PostMapping("/comments/{id}/update")
    public String update(@PathVariable("id") long id, @ModelAttribute @Valid CommentCreateEditDto commentDto,
                         BindingResult bindingResult, RedirectAttributes redirectAttributes,
                         @AuthenticationPrincipal UserDetails userDetails) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/comments/" + id;
        }
        CommentReadDto comment = commentService.update(id, commentDto, userDetails);
        return "redirect:/comments?bookId=" + comment.getBookId();
    }

    @PreAuthorize("@commentServiceImpl.checkCommentOwner(#id, principal)")
    @PostMapping("/comments/{id}/delete")
    public String delete(@PathVariable("id") long id) {
        CommentReadDto comment = commentService.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment with id %d not found".formatted(id)));
        commentService.deleteById(id);
        return "redirect:/comments?bookId=" + comment.getBookId();
    }
}
