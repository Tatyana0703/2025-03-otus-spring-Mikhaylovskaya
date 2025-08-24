package ru.otus.example.springbatch.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "books")
public class BookMongo {

    @Id
    private String id;

    private String title;

    @DBRef
    private AuthorMongo author;

    @DBRef
    private GenreMongo genre;

    public BookMongo(String title, AuthorMongo author, GenreMongo genre) {
        this.title = title;
        this.author = author;
        this.genre = genre;
    }
}