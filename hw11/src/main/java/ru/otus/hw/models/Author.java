package ru.otus.hw.models;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Table("authors")
public class Author {

    @Id
    private Long id;

    @NotBlank
    private String fullName;
}