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
import ru.otus.example.springbatch.h2.CommentTransformation;
import ru.otus.example.springbatch.mongo.CommentMongo;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
@Slf4j
@Configuration
public class CommentConfig {

    private static final int CHUNK_SIZE = 5;

    @StepScope
    @Bean
    public MongoPagingItemReader<CommentMongo> commentReader(MongoOperations mongoOperations) {
        return new MongoPagingItemReaderBuilder<CommentMongo>()
                .name("commentMongoItemReader")
                .template(mongoOperations)
                .jsonQuery("{}")
                .targetType(CommentMongo.class)
                .pageSize(CHUNK_SIZE)
                .sorts(new HashMap<>())
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<CommentMongo, CommentTransformation> commentProcessor() {
        return commentMongo -> new CommentTransformation(commentMongo.getText(), commentMongo.getBook().getId());
    }

    @StepScope
    @Bean
    public ItemWriter<CommentTransformation> commentWriter(NamedParameterJdbcOperations namedParameterJdbcOperations) {
        JdbcBatchItemWriter<CommentTransformation> writer = new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setJdbcTemplate(namedParameterJdbcOperations);
        writer.setSql(
                """
                insert into comments (text, book_id)
                values (:text,
                select id from tempbooks where mongo_id = :bookMongoId)
                """
        );
        return writer;
    }

    @Bean
    public Step transformCommentsStep(ItemReader<CommentMongo> commentReader,
                                      ItemWriter<CommentTransformation> commentWriter,
                                      ItemProcessor<CommentMongo, CommentTransformation> commentProcessor,
                                      JobRepository jobRepository,
                                      PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("transformCommentsStep", jobRepository)
                .<CommentMongo, CommentTransformation>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(commentReader)
                .processor(commentProcessor)
                .writer(commentWriter)
                .listener(new ItemReadListener<CommentMongo>() {
                    public void beforeRead() {
                        log.info("Начало чтения");
                    }

                    public void afterRead(@NonNull CommentMongo o) {
                        log.info("Конец чтения");
                    }

                    public void onReadError(@NonNull CommentMongo e) {
                        log.info("Ошибка чтения");
                    }
                })
                .listener(new ItemWriteListener<CommentTransformation>() {
                    public void beforeWrite(@NonNull List<CommentTransformation> list) {
                        log.info("Начало записи");
                    }

                    public void afterWrite(@NonNull List<CommentTransformation> list) {
                        log.info("Конец записи");
                    }

                    public void onWriteError(@NonNull Exception e, @NonNull List<CommentTransformation> list) {
                        log.info("Ошибка записи");
                    }
                })
                .listener(new ItemProcessListener<CommentMongo, CommentTransformation>() {
                    public void beforeProcess(@NonNull CommentMongo o) {
                        log.info("Начало обработки");
                    }

                    public void afterProcess(@NonNull CommentMongo o, CommentTransformation o2) {
                        log.info("Конец обработки");
                    }

                    public void onProcessError(@NonNull CommentMongo o, @NonNull Exception e) {
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
