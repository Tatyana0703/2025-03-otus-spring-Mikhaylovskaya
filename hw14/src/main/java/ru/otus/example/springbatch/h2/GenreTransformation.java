package ru.otus.example.springbatch.h2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GenreTransformation {

    private String name;

    private String genreMongoId;
}
