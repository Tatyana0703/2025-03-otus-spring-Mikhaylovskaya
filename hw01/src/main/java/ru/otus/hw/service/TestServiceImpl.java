package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        var testQuestions = questionDao.findAll();
        testQuestions.forEach(question -> {
            ioService.printLine(question.text());
            ioService.printLine("Answers:");
            question.answers().forEach(answer ->
                ioService.printLine(answer.text())
            );
            ioService.printLine("");
        });
    }
}