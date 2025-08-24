package ru.otus.example.springbatch.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.example.springbatch.config.AppProps;
import ru.otus.example.springbatch.service.IOService;
import java.util.Properties;
import static ru.otus.example.springbatch.config.JobConfig.IMPORT_BOOK_JOB_NAME;

@RequiredArgsConstructor
@ShellComponent
public class BatchCommands {

    public static final String JOB_PARAMETER = "trial";

    private final AppProps appProps;

    private final Job importBookJob;

    private final JobLauncher jobLauncher;

    private final JobOperator jobOperator;

    private final JobExplorer jobExplorer;

    private final IOService ioService;

    @SuppressWarnings("unused")
    @ShellMethod(value = "startMigrationJobWithJobLauncher", key = "sm-jl")
    public void startMigrationJobWithJobLauncher() throws Exception {
        JobExecution execution = jobLauncher.run(importBookJob, new JobParametersBuilder()
                .addString(JOB_PARAMETER, appProps.getTrial())
                .toJobParameters());
        ioService.printLine(execution.toString());
    }

    @SuppressWarnings("unused")
    @ShellMethod(value = "startMigrationJobWithJobOperator", key = "sm-jo")
    public void startMigrationJobWithJobOperator() throws Exception {
        Properties properties = new Properties();
        properties.put(JOB_PARAMETER, appProps.getTrial());

        Long executionId = jobOperator.start(IMPORT_BOOK_JOB_NAME, properties);
        ioService.printLine(jobOperator.getSummary(executionId));
    }
}