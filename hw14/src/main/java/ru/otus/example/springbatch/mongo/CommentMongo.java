package ru.otus.example.springbatch.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "comments")
public class CommentMongo {

    @Id
    private String id;

    private String text;

    @DBRef
    private BookMongo book;

    public CommentMongo(String text, BookMongo book) {
        this.text = text;
        this.book = book;
    }
}