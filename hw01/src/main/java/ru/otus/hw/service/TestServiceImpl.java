package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;

import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        var testQuestions = questionDao.findAll();
        printAllQuestions(testQuestions);
    }

    private void printAllQuestions(List<Question> questions) {
        questions.forEach(question -> {
            ioService.printLine(question.text());
            ioService.printLine("Answers:");
            question.answers().forEach(answer ->
                    ioService.printLine(answer.text())
            );
            ioService.printLine("");
        });
    }
}