package ru.otus.hw.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "comments")
public class Comment {

    @Id
    private String id;

    private String text;

    @DBRef
    private Book book;

    public Comment(String text, Book book) {
        this.text = text;
        this.book = book;
    }
}

