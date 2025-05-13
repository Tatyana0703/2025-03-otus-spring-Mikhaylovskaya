package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

@RequiredArgsConstructor
@Service
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question: questions) {
            askQuestion(question);
            var isAnswerValid = isInputAnswerValid(question);
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private void askQuestion(Question question) {
        ioService.printLine(question.text());
        ioService.printLine("Answers:");
        var answers = question.answers();
        for (var i = 0; i < answers.size(); i++) {
            String outputQuestion = convertQuestionToString(i + 1, question);
            ioService.printLine(outputQuestion);
        }
    }

    private String convertQuestionToString(int sequenceNumber, Question question) {
        return String.format("%s. %s", sequenceNumber, question.answers().get(sequenceNumber - 1).text());
    }

    private boolean isInputAnswerValid(Question question) {
        var numberOfAnswer = ioService.readIntForRangeWithPrompt(1,
            question.answers().size(),
                "Please, enter the number of answer from the offered:",
                "The response number you entered is incorrect. Please, re-enter:");
        return question.answers().get(numberOfAnswer - 1).isCorrect();
    }
}