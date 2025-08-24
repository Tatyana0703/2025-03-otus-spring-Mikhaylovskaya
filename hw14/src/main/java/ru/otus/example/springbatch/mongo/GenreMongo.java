package ru.otus.example.springbatch.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "genres")
public class GenreMongo {

    @Id
    private String id;

    private String name;

    public GenreMongo(String name) {
        this.name = name;
    }
}