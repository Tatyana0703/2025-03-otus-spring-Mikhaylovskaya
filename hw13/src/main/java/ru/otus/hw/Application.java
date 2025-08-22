package ru.otus.hw;

import org.h2.tools.Console;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.sql.SQLException;

@SpringBootApplication
public class Application {

    public static void main(String[] args) throws SQLException {
        // http://localhost:8080/books

        //обновление и удаление комментария для книги доступно только создателю этого комментария

        //пользователь с комментариями к книге с идентификатором 1
        // login: username_1 , password: 123

        //пользователь с комментариями к книге с идентификатором 2
        // login: username_2 , password: 123

        //по кнопке Registration можно создать нового пользователя и добавить ему комментарий

        SpringApplication.run(Application.class, args);

        Console.main(args);
    }
}