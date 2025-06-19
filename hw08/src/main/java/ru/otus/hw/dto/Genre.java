package ru.otus.hw.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Genre {
    @Id
    private String name;
}