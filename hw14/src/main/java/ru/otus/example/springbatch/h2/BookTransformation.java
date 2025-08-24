package ru.otus.example.springbatch.h2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookTransformation {

    private String title;

    private String bookMongoId;

    private String authorMongoId;

    private String genreMongoId;
}
