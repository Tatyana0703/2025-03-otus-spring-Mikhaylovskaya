package ru.otus.example.springbatch.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoPagingItemReader;
import org.springframework.batch.item.data.builder.MongoPagingItemReaderBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.lang.NonNull;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.example.springbatch.h2.BookTransformation;
import ru.otus.example.springbatch.mongo.BookMongo;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
@Slf4j
@Configuration
public class BookConfig {

    private static final int CHUNK_SIZE = 5;

    @StepScope
    @Bean
    public MongoPagingItemReader<BookMongo> bookReader(MongoOperations mongoOperations) {
        return new MongoPagingItemReaderBuilder<BookMongo>()
                .name("bookMongoItemReader")
                .template(mongoOperations)
                .jsonQuery("{}")
                .targetType(BookMongo.class)
                .pageSize(CHUNK_SIZE)
                .sorts(new HashMap<>())
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<BookMongo, BookTransformation> bookProcessor() {
        return bookMongo -> new BookTransformation(bookMongo.getTitle(),
                bookMongo.getId(),
                bookMongo.getAuthor().getId(),
                bookMongo.getGenre().getId());
    }

    @StepScope
    @Bean
    public ItemWriter<BookTransformation> bookWriter(NamedParameterJdbcOperations namedParameterJdbcOperations) {
        JdbcBatchItemWriter<BookTransformation> writer = new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setJdbcTemplate(namedParameterJdbcOperations);
        writer.setSql(
                """
                insert into tempbooks(id, mongo_id)
                select id, :bookMongoId from final table 
                ( 
                insert into books (title, author_id, genre_id) 
                values (:title, 
                select id from tempauthors where mongo_id = :authorMongoId, 
                select id from tempgenres where mongo_id = :genreMongoId) 
                )
                """
        );
        return writer;
    }

    @Bean
    public Step transformBooksStep(ItemReader<BookMongo> bookReader, ItemWriter<BookTransformation> bookWriter,
                                    ItemProcessor<BookMongo, BookTransformation> bookProcessor,
                                    JobRepository jobRepository,
                                    PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("transformBooksStep", jobRepository)
                .<BookMongo, BookTransformation>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(bookReader)
                .processor(bookProcessor)
                .writer(bookWriter)
                .listener(new ItemReadListener<BookMongo>() {
                    public void beforeRead() {
                        log.info("Начало чтения");
                    }

                    public void afterRead(@NonNull BookMongo o) {
                        log.info("Конец чтения");
                    }

                    public void onReadError(@NonNull BookMongo e) {
                        log.info("Ошибка чтения");
                    }
                })
                .listener(new ItemWriteListener<BookTransformation>() {
                    public void beforeWrite(@NonNull List<BookTransformation> list) {
                        log.info("Начало записи");
                    }

                    public void afterWrite(@NonNull List<BookTransformation> list) {
                        log.info("Конец записи");
                    }

                    public void onWriteError(@NonNull Exception e, @NonNull List<BookTransformation> list) {
                        log.info("Ошибка записи");
                    }
                })
                .listener(new ItemProcessListener<BookMongo, BookTransformation>() {
                    public void beforeProcess(@NonNull BookMongo o) {
                        log.info("Начало обработки");
                    }

                    public void afterProcess(@NonNull BookMongo o, BookTransformation o2) {
                        log.info("Конец обработки");
                    }

                    public void onProcessError(@NonNull BookMongo o, @NonNull Exception e) {
                        log.info("Ошибка обработки");
                    }
                })
                .listener(new ChunkListener() {
                    public void beforeChunk(@NonNull ChunkContext chunkContext) {
                        log.info("Начало пачки");
                    }

                    public void afterChunk(@NonNull ChunkContext chunkContext) {
                        log.info("Конец пачки");
                    }

                    public void afterChunkError(@NonNull ChunkContext chunkContext) {
                        log.info("Ошибка пачки");
                    }
                })
                .build();
    }
}
