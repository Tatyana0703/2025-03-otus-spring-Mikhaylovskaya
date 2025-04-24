package ru.otus.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TestServiceImplTest {

    @Mock
    private IOService ioService;
    @Mock
    private QuestionDao questionDao;
    @InjectMocks
    private TestServiceImpl testService;
    @Captor
    private ArgumentCaptor<String> strCaptor;

    @DisplayName("должен выводить вопросы с вариантами ответов")
    @Test
    void shouldCorrectExecuteTestWithSeveralQuestionsAndAnswers() {
        var questions = List.of(
                new Question("question 1", List.of(
                        new Answer("answer 1", true),
                        new Answer("answer 2", false))
                ),
                new Question("question 2", List.of(
                        new Answer("answer 3", true),
                        new Answer("answer 4", false))
                ));
        given(questionDao.findAll()).willReturn(questions);
        willDoNothing().given(ioService).printLine(strCaptor.capture());
        testService.executeTest();
        verify(questionDao, times(1)).findAll();
        verify(ioService, times(1)).printFormattedLine(any());
        verify(ioService, times(11)).printLine(any(String.class));
        var actualPrintedLines = strCaptor.getAllValues();
        assertThat(actualPrintedLines).hasSize(11);
        questions.forEach(question -> {
            var questionAnswers = question.answers().stream().map(Answer::text).toList();
            var questionWithAnswers = Stream.of(question.text(), "Answers:").collect(Collectors.toList());
            questionWithAnswers.addAll(questionAnswers);
            assertThat(actualPrintedLines).containsSequence(questionWithAnswers);
        });
    }

    @DisplayName("должен корректно обрабатывать пустой список вопросов")
    @Test
    void shouldCorrectExecuteTestWithEmptyListOfQuestions() {
        given(questionDao.findAll()).willReturn(Collections.emptyList());
        InOrder inOrder = inOrder(ioService, questionDao);
        testService.executeTest();
        inOrder.verify(ioService, times(1)).printLine(any());
        inOrder.verify(ioService, times(1)).printFormattedLine(any());
        inOrder.verify(questionDao, times(1)).findAll();
    }
}