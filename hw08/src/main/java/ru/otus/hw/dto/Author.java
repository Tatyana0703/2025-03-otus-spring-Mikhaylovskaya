package ru.otus.hw.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Author {
    @Id
    private String fullName;
}
