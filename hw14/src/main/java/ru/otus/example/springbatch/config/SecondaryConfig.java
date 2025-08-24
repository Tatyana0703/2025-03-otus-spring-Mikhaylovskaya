package ru.otus.example.springbatch.config;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.example.springbatch.service.CleanUpService;

@SuppressWarnings("unused")
@Configuration
public class SecondaryConfig {

    @Bean
    public Step prepareStep(CleanUpService cleanUpService,
                            JobRepository jobRepository,
                            PlatformTransactionManager platformTransactionManager) {
        MethodInvokingTaskletAdapter adapter = new MethodInvokingTaskletAdapter();
        adapter.setTargetObject(cleanUpService);
        adapter.setTargetMethod("prepare");

        return new StepBuilder("prepareStep", jobRepository)
                .tasklet(adapter, platformTransactionManager)
                .build();
    }

    @Bean
    public Step cleanUpStep(CleanUpService cleanUpService,
                            JobRepository jobRepository,
                            PlatformTransactionManager platformTransactionManager) {
        MethodInvokingTaskletAdapter adapter = new MethodInvokingTaskletAdapter();
        adapter.setTargetObject(cleanUpService);
        adapter.setTargetMethod("cleanUp");

        return new StepBuilder("cleanUpStep", jobRepository)
                .tasklet(adapter, platformTransactionManager)
                .build();
    }
}
