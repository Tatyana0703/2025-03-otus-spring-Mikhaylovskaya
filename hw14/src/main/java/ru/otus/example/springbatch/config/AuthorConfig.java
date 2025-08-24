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
import ru.otus.example.springbatch.h2.AuthorTransformation;
import ru.otus.example.springbatch.mongo.AuthorMongo;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
@Slf4j
@Configuration
public class AuthorConfig {

    private static final int CHUNK_SIZE = 5;

    @StepScope
    @Bean
    public MongoPagingItemReader<AuthorMongo> authorReader(MongoOperations mongoOperations) {
        return new MongoPagingItemReaderBuilder<AuthorMongo>()
                .name("authorMongoItemReader")
                .template(mongoOperations)
                .jsonQuery("{}")
                .targetType(AuthorMongo.class)
                .pageSize(CHUNK_SIZE)
                .sorts(new HashMap<>())
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<AuthorMongo, AuthorTransformation> authorProcessor() {
        return authorMongo -> new AuthorTransformation(authorMongo.getFullName(), authorMongo.getId());
    }

    @StepScope
    @Bean
    public ItemWriter<AuthorTransformation> authorWriter(NamedParameterJdbcOperations namedParameterJdbcOperations) {
        JdbcBatchItemWriter<AuthorTransformation> writer = new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setJdbcTemplate(namedParameterJdbcOperations);
        writer.setSql(
            """
            insert into tempauthors(id, mongo_id) 
            select id, :authorMongoId from final table 
            ( insert into authors (full_name) values (:fullName) )
            """
        );
        return writer;
    }

    @Bean
    public Step transformAuthorsStep(ItemReader<AuthorMongo> authorReader, ItemWriter<AuthorTransformation> authorWriter,
                                     ItemProcessor<AuthorMongo, AuthorTransformation> authorProcessor,
                                     JobRepository jobRepository,
                                     PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("transformAuthorsStep", jobRepository)
                .<AuthorMongo, AuthorTransformation>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(authorReader)
                .processor(authorProcessor)
                .writer(authorWriter)
                .listener(new ItemReadListener<AuthorMongo>() {
                    public void beforeRead() {
                        log.info("Начало чтения");
                    }

                    public void afterRead(@NonNull AuthorMongo o) {
                        log.info("Конец чтения");
                    }

                    public void onReadError(@NonNull AuthorMongo e) {
                        log.info("Ошибка чтения");
                    }
                })
                .listener(new ItemWriteListener<AuthorTransformation>() {
                    public void beforeWrite(@NonNull List<AuthorTransformation> list) {
                        log.info("Начало записи");
                    }

                    public void afterWrite(@NonNull List<AuthorTransformation> list) {
                        log.info("Конец записи");
                    }

                    public void onWriteError(@NonNull Exception e, @NonNull List<AuthorTransformation> list) {
                        log.info("Ошибка записи");
                    }
                })
                .listener(new ItemProcessListener<AuthorMongo, AuthorTransformation>() {
                    public void beforeProcess(@NonNull AuthorMongo o) {
                        log.info("Начало обработки");
                    }

                    public void afterProcess(@NonNull AuthorMongo o, AuthorTransformation o2) {
                        log.info("Конец обработки");
                    }

                    public void onProcessError(@NonNull AuthorMongo o, @NonNull Exception e) {
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
