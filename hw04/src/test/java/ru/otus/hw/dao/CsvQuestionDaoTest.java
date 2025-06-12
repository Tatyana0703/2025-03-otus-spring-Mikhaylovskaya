package ru.otus.hw.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.BDDMockito.given;

@SpringBootTest(classes = {CsvQuestionDao.class})
class CsvQuestionDaoTest {

    private static final String CORRECT_TEST_FILE = "test.csv";
    private static final String INCORRECT_TEST_FILE = "test-2.csv";

    @MockitoBean
    private TestFileNameProvider fileNameProvider;

    @Autowired
    private CsvQuestionDao questionDao;

    @DisplayName("должен корректно возвращать вопросы с ответами из csv-файла")
    @Test
    void shouldCorrectFindAllQuestionsWithAnswersFromCsvFile() {
        given(fileNameProvider.getTestFileName())
                .willReturn(CORRECT_TEST_FILE);
        List<Question> expectedQuestions = getExpectedQuestionsFromCsv();

        var actualQuestions = questionDao.findAll();

        assertThat(actualQuestions).hasSize(expectedQuestions.size())
                .containsExactlyInAnyOrderElementsOf(expectedQuestions);
    }

    @DisplayName("должен возвращать exception, если csv-файл не найден в ресурсах")
    @Test
    void shouldReturnExceptionIfCsvFileNotFound() {
        given(fileNameProvider.getTestFileName())
                .willReturn(INCORRECT_TEST_FILE);

        assertThatCode(() -> questionDao.findAll())
                .isInstanceOf(QuestionReadException.class);
    }

    private List<Question> getExpectedQuestionsFromCsv() {
        return List.of(
                new Question("question 1", List.of(
                        new Answer("answer 1-1", false),
                        new Answer("answer 1-2", true)
                )),
                new Question("question 2", List.of(
                        new Answer("answer 2-1", false),
                        new Answer("answer 2-2", false),
                        new Answer("answer 2-3", true)
                )));
    }
}