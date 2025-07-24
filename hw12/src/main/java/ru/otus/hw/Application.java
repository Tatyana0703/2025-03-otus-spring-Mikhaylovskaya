package ru.otus.hw;

import org.h2.tools.Console;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.sql.SQLException;

@SpringBootApplication
public class Application {

    public static void main(String[] args) throws SQLException {
        // http://localhost:8080/books
        // login: test_username
        // password: 123
        SpringApplication.run(Application.class, args);

        Console.main(args);
    }
}