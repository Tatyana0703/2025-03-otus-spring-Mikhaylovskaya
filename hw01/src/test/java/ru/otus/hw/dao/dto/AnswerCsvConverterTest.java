package ru.otus.hw.dao.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import ru.otus.hw.domain.Answer;
import org.junit.jupiter.params.ParameterizedTest;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AnswerCsvConverterTest {

    private final AnswerCsvConverter answerCsvConverter = new AnswerCsvConverter();

    @DisplayName("должен корректно конвертировать входной параметр в объект ответа")
    @ParameterizedTest(name = "входной ответ: {0}, ожидаемый результат: {1}")
    @MethodSource("getValidArguments")
    void shouldCorrectConvertReadParameterToAnswer(String inputTextAnswer, Answer expectedAnswer) {
        var actualAnswer = answerCsvConverter.convertToRead(inputTextAnswer);
        assertThat(actualAnswer).isNotNull()
                .isInstanceOf(Answer.class)
                .isEqualTo(expectedAnswer);
    }

    static Stream<Arguments> getValidArguments() {
        return Stream.of(
                Arguments.of("text answer%true", new Answer("text answer", true)),
                Arguments.of("text answer%false", new Answer("text answer", false)),
                Arguments.of("%true", new Answer("", true)),
                Arguments.of("%false", new Answer("", false))
        );
    }

    @DisplayName("должен выдавать ошибку при конвертации невалидной строки в объект ответа")
    @ParameterizedTest(name = "входной ответ: {0}")
    @CsvSource({"text answer%", "text answer"})
    void shouldThrowExceptionIfInputTextAnswerInvalid(String inputTextAnswer) {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> answerCsvConverter.convertToRead(inputTextAnswer));
    }
}
