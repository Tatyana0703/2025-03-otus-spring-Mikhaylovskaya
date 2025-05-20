package ru.otus.hw.repositories.annotation;

import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.repositories.JdbcBookRepository;
import ru.otus.hw.repositories.JdbcGenreRepository;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@JdbcTest
@Import({JdbcBookRepository.class, JdbcGenreRepository.class})
public @interface BookDaoTest {
}
