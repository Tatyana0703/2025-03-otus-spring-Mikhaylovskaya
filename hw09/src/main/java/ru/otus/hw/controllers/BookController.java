package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.otus.hw.dto.BookReadDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.dto.BookCreateEditDto;
import ru.otus.hw.services.GenreService;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    @GetMapping("/books")
    public String findAll(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "book/list";
    }

    @GetMapping("/books/{id}")
    public String findById(@PathVariable("id") long id, Model model) {
        BookReadDto book = bookService.findById(id)
                .orElseThrow(() -> new NotFoundException("Book with id %d not found".formatted(id)));
        model.addAttribute("book", book);
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        return "book/edit";
    }

    @GetMapping("/books/registration")
    public String registration(Model model, @ModelAttribute("book") BookCreateEditDto bookDto) {
        model.addAttribute("book", bookDto);
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        return "book/registration";
    }

    @PostMapping("/books")
    public String create(@ModelAttribute @Valid BookCreateEditDto bookDto, BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("book", bookDto);
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/books/registration";
        }
        BookReadDto book = bookService.create(bookDto);
        return "redirect:/books";
    }

    @PostMapping("/books/{id}/update")
    public String update(@PathVariable("id") long id, @ModelAttribute @Valid BookCreateEditDto bookDto,
                         BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/books/" + id;
        }
        BookReadDto book = bookService.update(id, bookDto)
                .orElseThrow(() -> new NotFoundException("Book with id %d not found".formatted(id)));
        return "redirect:/books";
    }

    @PostMapping("/books/{id}/delete")
    public String delete(@PathVariable("id") long id) {
        bookService.deleteById(id);
        return "redirect:/books";
    }
}
