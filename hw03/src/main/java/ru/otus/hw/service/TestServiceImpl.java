package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final LocalizedIOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printLineLocalized("TestService.answer.the.questions");
        ioService.printLine("");
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question: questions) {
            askQuestion(question);
            var isAnswerValid = readAndValidateAnswerFor(question);
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private void askQuestion(Question question) {
        ioService.printLine(question.text());
        ioService.printLineLocalized("TestService.answers.header");
        var answers = question.answers();
        for (var i = 0; i < answers.size(); i++) {
            String outputQuestion = convertQuestionToString(i + 1, question);
            ioService.printLine(outputQuestion);
        }
    }

    private String convertQuestionToString(int sequenceNumber, Question question) {
        return String.format("%s. %s", sequenceNumber, question.answers().get(sequenceNumber - 1).text());
    }

    private boolean readAndValidateAnswerFor(Question question) {
        var numberOfAnswer = ioService.readIntForRangeWithPromptLocalized(1,
                question.answers().size(),
                "TestService.enter.answer.number",
                "TestService.reenter.answer.number");
        return question.answers().get(numberOfAnswer - 1).isCorrect();
    }
}