package ru.otus.example.springbatch.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.otus.example.springbatch.mongo.AuthorMongo;
import ru.otus.example.springbatch.mongo.BookMongo;
import ru.otus.example.springbatch.mongo.CommentMongo;
import ru.otus.example.springbatch.mongo.GenreMongo;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.otus.example.springbatch.config.JobConfig.IMPORT_BOOK_JOB_NAME;
import static ru.otus.example.springbatch.shell.BatchCommands.JOB_PARAMETER;

@SpringBootTest
@SpringBatchTest
class ImportBookJobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AppProps appProps;

    @BeforeEach
    void clearMetaData() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    void testJob() throws Exception {
        Job job = jobLauncherTestUtils.getJob();
        assertThat(job).isNotNull()
                .extracting(Job::getName)
                .isEqualTo(IMPORT_BOOK_JOB_NAME);

        JobParameters parameters = new JobParametersBuilder()
                .addString(JOB_PARAMETER, appProps.getTrial())
                .toJobParameters();
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(parameters);

        assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");

        long mongoBookCount = mongoTemplate.count(new Query(), BookMongo.class);
        int h2BookCount = requireNonNull(jdbcTemplate.queryForObject("select count(*) from books", Integer.class));
        assertThat(h2BookCount).isEqualTo(mongoBookCount);

        long mongoAuthorCount = mongoTemplate.count(new Query(), AuthorMongo.class);
        int h2AuthorCount = requireNonNull(jdbcTemplate.queryForObject("select count(*) from authors", Integer.class));
        assertThat(h2AuthorCount).isEqualTo(mongoAuthorCount);

        long mongoGenreCount = mongoTemplate.count(new Query(), GenreMongo.class);
        int h2GenreCount = requireNonNull(jdbcTemplate.queryForObject("select count(*) from genres", Integer.class));
        assertThat(h2GenreCount).isEqualTo(mongoGenreCount);

        long mongoCommentCount = mongoTemplate.count(new Query(), CommentMongo.class);
        int h2CommentCount = requireNonNull(jdbcTemplate.queryForObject("select count(*) from comments", Integer.class));
        assertThat(h2CommentCount).isEqualTo(mongoCommentCount);
    }
}