package ru.otus.example.springbatch.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.lang.NonNull;
import java.util.Map;

@SuppressWarnings("unused")
@Slf4j
@Configuration
public class JobConfig {

    public static final String IMPORT_BOOK_JOB_NAME = "importBookJob";

    private static final int CHUNK_SIZE = 5;

    @Bean
    public Job importBookJob(Map<String, Step> steps,
                             JobRepository jobRepository) {
        return new JobBuilder(IMPORT_BOOK_JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(prepareFlow(steps.get("prepareStep")))
                .next(splitFlow(steps.get("transformAuthorsStep"), steps.get("transformGenresStep")))
                .next(steps.get("transformBooksStep"))
                .next(steps.get("transformCommentsStep"))
                .next(steps.get("cleanUpStep"))
                .end()
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(@NonNull JobExecution jobExecution) {
                        log.info("Начало job");
                    }

                    @Override
                    public void afterJob(@NonNull JobExecution jobExecution) {
                        log.info("Конец job");
                    }
                })
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor("spring_batch");
    }

    @Bean
    public Flow prepareFlow(Step prepareStep) {
        return new FlowBuilder<SimpleFlow>("prepare-flow")
            .start(prepareStep)
            .build();
    }

    @Bean
    public Flow splitFlow(Step transformAuthorsStep, Step transformGenresStep) {
        return new FlowBuilder<SimpleFlow>("splitFlow")
                .split(taskExecutor())
                .add(authorFlow(transformAuthorsStep), genreFlow(transformGenresStep))
                .build();
    }

    @Bean
    public Flow authorFlow(Step transformAuthorsStep) {
        return new FlowBuilder<SimpleFlow>("authorFlow")
                .start(transformAuthorsStep)
                .build();
    }

    @Bean
    public Flow genreFlow(Step transformGenresStep) {
        return new FlowBuilder<SimpleFlow>("genreFlow")
                .start(transformGenresStep)
                .build();
    }
}