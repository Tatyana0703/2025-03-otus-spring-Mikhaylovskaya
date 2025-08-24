package ru.otus.example.springbatch;

import com.github.cloudyrock.spring.v5.EnableMongock;
import org.h2.tools.Console;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.sql.SQLException;

@EnableMongock
@SpringBootApplication
public class Main {

    public static void main(String[] args) throws SQLException {
        //миграция данных из NoSQL в реляционную базу данных

        Console.main(args);

        SpringApplication.run(Main.class, args);
    }
}
