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
import ru.otus.example.springbatch.h2.GenreTransformation;
import ru.otus.example.springbatch.mongo.GenreMongo;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
@Slf4j
@Configuration
public class GenreConfig {

    private static final int CHUNK_SIZE = 5;

    @StepScope
    @Bean
    public MongoPagingItemReader<GenreMongo> genreReader(MongoOperations mongoOperations) {
        return new MongoPagingItemReaderBuilder<GenreMongo>()
                .name("genreMongoItemReader")
                .template(mongoOperations)
                .jsonQuery("{}")
                .targetType(GenreMongo.class)
                .pageSize(CHUNK_SIZE)
                .sorts(new HashMap<>())
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<GenreMongo, GenreTransformation> genreProcessor() {
        return genreMongo -> new GenreTransformation(genreMongo.getName(), genreMongo.getId());
    }

    @StepScope
    @Bean
    public ItemWriter<GenreTransformation> genreWriter(NamedParameterJdbcOperations namedParameterJdbcOperations) {
        JdbcBatchItemWriter<GenreTransformation> writer = new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setJdbcTemplate(namedParameterJdbcOperations);
        writer.setSql(
                """
                insert into tempgenres(id, mongo_id)
                select id, :genreMongoId from final table
                ( insert into genres (name) values (:name) )
                """
        );
        return writer;
    }

    @Bean
    public Step transformGenresStep(ItemReader<GenreMongo> genreReader, ItemWriter<GenreTransformation> genreWriter,
                                     ItemProcessor<GenreMongo, GenreTransformation> genreProcessor,
                                     JobRepository jobRepository,
                                     PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("transformGenresStep", jobRepository)
                .<GenreMongo, GenreTransformation>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(genreReader)
                .processor(genreProcessor)
                .writer(genreWriter)
                .listener(new ItemReadListener<GenreMongo>() {
                    public void beforeRead() {
                        log.info("Начало чтения");
                    }

                    public void afterRead(@NonNull GenreMongo o) {
                        log.info("Конец чтения");
                    }

                    public void onReadError(@NonNull GenreMongo e) {
                        log.info("Ошибка чтения");
                    }
                })
                .listener(new ItemWriteListener<GenreTransformation>() {
                    public void beforeWrite(@NonNull List<GenreTransformation> list) {
                        log.info("Начало записи");
                    }

                    public void afterWrite(@NonNull List<GenreTransformation> list) {
                        log.info("Конец записи");
                    }

                    public void onWriteError(@NonNull Exception e, @NonNull List<GenreTransformation> list) {
                        log.info("Ошибка записи");
                    }
                })
                .listener(new ItemProcessListener<GenreMongo, GenreTransformation>() {
                    public void beforeProcess(@NonNull GenreMongo o) {
                        log.info("Начало обработки");
                    }

                    public void afterProcess(@NonNull GenreMongo o, GenreTransformation o2) {
                        log.info("Конец обработки");
                    }

                    public void onProcessError(@NonNull GenreMongo o, @NonNull Exception e) {
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
