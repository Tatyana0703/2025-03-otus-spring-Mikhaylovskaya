package ru.otus.hw.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CsvQuestionDaoTest {

    private static final String QUESTION_SEPARATOR = ";";
    private static final String ANSWER_SEPARATOR = "\\|";
    private static final String ANSWER_PROPERTY_SEPARATOR = "%";
    private static final int SKIP_LINES_COUNT = 1;
    private static final String CORRECT_TEST_FILE = "test.csv";
    private static final String INCORRECT_TEST_FILE = "test-2.csv";

    @Mock
    private TestFileNameProvider fileNameProvider;

    @InjectMocks
    private CsvQuestionDao questionDao;

    @DisplayName("должен корректно возвращать вопросы с ответами из csv-файла")
    @Test
    void shouldCorrectFindAllQuestionsWithAnswersFromCsvFile() throws IOException {
        var csvFilename = CORRECT_TEST_FILE;
        given(fileNameProvider.getTestFileName())
                .willReturn(csvFilename);
        List<Question> expectedQuestions = getExpectedQuestionsFromCsv(csvFilename);

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

    private List<Question> getExpectedQuestionsFromCsv(String csvFilename) throws IOException {
        List<Question> expectedQuestions;
        try (InputStream inputStream = CsvQuestionDaoTest.class.getClassLoader().getResourceAsStream(csvFilename);
             BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)));
             Stream<String> lines = reader.lines()) {
            expectedQuestions = lines
                    .skip(SKIP_LINES_COUNT)
                    .map(line -> {
                        var arrQuestion = line.split(QUESTION_SEPARATOR);
                        var answers = Arrays.stream(arrQuestion[1].split(ANSWER_SEPARATOR))
                                .map(this::apply)
                                .toList();
                        return new Question(arrQuestion[0], answers);
                    }).toList();
        }
        return expectedQuestions;
    }

    private Answer apply(String textAnswer) {
        return new Answer(textAnswer.split(ANSWER_PROPERTY_SEPARATOR)[0],
                Boolean.parseBoolean(textAnswer.split(ANSWER_PROPERTY_SEPARATOR)[1]));
    }
}