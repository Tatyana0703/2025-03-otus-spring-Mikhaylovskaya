package ru.otus.example.springbatch.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CleanUpService {

    private final JdbcOperations jdbcOperations;

    @SuppressWarnings("unused")
    public void prepare() throws Exception {
        log.info("Выполняю подготовительные мероприятия...");

        String sql = """
                create table tempauthors (
                    id bigint,
                    mongo_id varchar(64)
                    );
                create table tempgenres (
                    id bigint,
                    mongo_id varchar(64)
                    );
                create table tempbooks (
                    id bigint,
                    mongo_id varchar(64)
                    );
                """;
        jdbcOperations.execute(sql);

        log.info("Завершающие подготовительные закончены");
    }

    @SuppressWarnings("unused")
    public void cleanUp() throws Exception {
        log.info("Выполняю завершающие мероприятия...");

        String sql = """
                drop table if exists tempbooks;
                drop table if exists tempauthors;
                drop table if exists tempgenres;
                """;
        jdbcOperations.execute(sql);

        log.info("Завершающие мероприятия закончены");
    }
}