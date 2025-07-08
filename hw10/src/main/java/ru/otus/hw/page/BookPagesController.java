package ru.otus.hw.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BookPagesController {

    @GetMapping(path = "/books", params = "!id")
    public String listBooksPage() {
        return "book/list";
    }

    @GetMapping("/books/registration")
    public String addBookPage() {
        return "book/registration";
    }

    @GetMapping(path = "/books", params = "id")
    public String editBookPage(@RequestParam String id) {
        return "book/edit";
    }
}
