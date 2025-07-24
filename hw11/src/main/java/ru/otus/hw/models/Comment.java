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
@Table("comments")
public class Comment {

    @Id
    private Long id;

    @NotBlank
    private String text;

    @NotNull
    private Long bookId;

    @PersistenceCreator
    private Comment(Long id, @NotBlank String text, @NotNull Long bookId) {
        this.id = id;
        this.text = text;
        this.bookId = bookId;
    }

    public Comment(String text, Long bookId) {
        this(null, text,  bookId);
    }
}
