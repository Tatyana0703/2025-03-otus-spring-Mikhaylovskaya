package ru.otus.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.*;

@SpringBootTest(classes = {TestServiceImpl.class})
class TestServiceImplTest {

    @MockitoBean
    private LocalizedIOService ioService;

    @MockitoBean
    private QuestionDao questionDao;

    @Autowired
    private TestServiceImpl testService;

    @Captor
    private ArgumentCaptor<String> textCaptor;

    private final Student student = new Student("test", "test");

    @DisplayName("должен корректно производить подсчет правильных ответов")
    @Test
    void shouldReturnCorrectTestResultBySomeAnswers() {
        List<Answer> chosenAnswers = getChosenAnswers();
        List<Question> allQuestions = getAllQuestions(chosenAnswers);
        List<Integer> chosenAnswerNumbers = getChosenAnswerNumbers(allQuestions, chosenAnswers);
        var expectedRightAnswersCount = chosenAnswers.stream()
                .map(answer -> answer.isCorrect() ? 1 : 0)
                .mapToInt(Integer::intValue)
                .sum();
        var expectedTestResult = new TestResult(student);
        expectedTestResult.setRightAnswersCount(expectedRightAnswersCount);

        given(questionDao.findAll()).willReturn(allQuestions);
        given(ioService.readIntForRangeWithPromptLocalized(anyInt(), anyInt(), any(String.class), any(String.class)))
                .willReturn(chosenAnswerNumbers.get(0))
                .willReturn(chosenAnswerNumbers.get(1))
                .willReturn(chosenAnswerNumbers.get(2));

        var actualResult = testService.executeTestFor(student);

        assertThat(actualResult).isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("answeredQuestions")
                .isEqualTo(expectedTestResult);
        assertThat(actualResult.getAnsweredQuestions())
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyElementsOf(allQuestions);
    }

    private List<Answer> getChosenAnswers() {
        return List.of(
                new Answer("answer 1", true),
                new Answer("answer 2", false),
                new Answer("answer 3", true)
        );
    }

    private List<Question> getAllQuestions(List<Answer> chosenAnswers) {
        return List.of(
                new Question("question 1", List.of(
                        new Answer("answer", true),
                        chosenAnswers.get(0))
                ),
                new Question("question 2", List.of(
                        chosenAnswers.get(1),
                        new Answer("answer", false))
                ),
                new Question("question 3", List.of(
                        new Answer("answer", true),
                        chosenAnswers.get(2))
                ));
    }

    private List<Integer> getChosenAnswerNumbers(List<Question> allQuestions, List<Answer> chosenAnswers) {
        List<Integer> chosenAnswerNumbers = new ArrayList<Integer>();
        for (int index = 0; index < allQuestions.size(); index++) {
            chosenAnswerNumbers.add(
                    allQuestions.get(index).answers().indexOf(chosenAnswers.get(index)) + 1
            );
        }
        return chosenAnswerNumbers;
    }

    @DisplayName("должен корректно выводить вопрос c вариантами ответов")
    @Test
    void correctPrintQuestionsWithAnswers() {
        List<Answer> chosenAnswers = getChosenAnswers();
        List<Question> allQuestions = getAllQuestions(chosenAnswers);
        given(questionDao.findAll()).willReturn(allQuestions);
        willDoNothing().given(ioService).printLine(textCaptor.capture());
        given(ioService.readIntForRangeWithPromptLocalized(anyInt(), anyInt(), any(String.class), any(String.class)))
                .willReturn(1);

        testService.executeTestFor(student);

        var actualPrintedLines = textCaptor.getAllValues();
        assertThat(actualPrintedLines).hasSizeGreaterThan(allQuestions.size());
        List<String> questionsWithAnswers = new ArrayList<>();
        allQuestions.forEach(question -> {
            questionsWithAnswers.add(question.text());
            var questionAnswers = question.answers().stream()
                    .map(answer -> String.format("%s. %s", question.answers().indexOf(answer) + 1, answer.text()))
                    .toList();
            questionsWithAnswers.addAll(questionAnswers);
        });
        assertThat(actualPrintedLines).containsSubsequence(questionsWithAnswers);
    }
}