package ru.otus.example.springbatch.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "authors")
public class AuthorMongo {

    @Id
    private String id;

    private String fullName;

    public AuthorMongo(String fullName) {
        this.fullName = fullName;
    }
}
