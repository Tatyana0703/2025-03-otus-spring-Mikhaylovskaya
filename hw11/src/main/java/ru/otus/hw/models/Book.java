package ru.otus.hw.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("books")
public class Book {

    @Id
    private Long id;

    @NotBlank
    private String title;

    @NotNull
    private Long authorId;

    @NotNull
    private Long genreId;

    @PersistenceCreator
    private Book(Long id, @NotBlank String title, @NotNull Long authorId, @NotNull Long genreId) {
        this.id = id;
        this.title = title;
        this.authorId = authorId;
        this.genreId = genreId;
    }

    public Book(String title, Long authorId, Long genreId) {
        this(null, title,  authorId, genreId);
    }
}