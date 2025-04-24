package ru.otus.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.PrintStream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StreamsIOServiceTest {

    @Mock
    private PrintStream printStream;
    @InjectMocks
    private StreamsIOService ioService;
    @Captor
    private ArgumentCaptor<String> strCaptor;
    @Captor
    private ArgumentCaptor<Object[]> objArrCaptor;

    @DisplayName("должен корректно выводить строку в поток")
    @Test
    void shouldCorrectOutputLineIntoStream() {
        var expectedText = "test";
        ioService.printLine(expectedText);

        verify(printStream, times(1)).println(any(String.class));
        verify(printStream).println(strCaptor.capture());
        var actualText = strCaptor.getValue();
        assertThat(actualText).isEqualTo(expectedText);
    }

    @DisplayName("должен корректно выводить форматировнную строку в поток")
    @Test
    void shouldCorrectOutputFormattedLineIntoStream() {
        var inputText = "test";
        var inputObject = new Object();
        ioService.printFormattedLine(inputText, inputObject);

        verify(printStream, times(1)).printf(any(String.class), any(Object.class));
        verify(printStream).printf(strCaptor.capture(), objArrCaptor.capture());
        var actualInputText = strCaptor.getValue();
        var actualInputObjArr = objArrCaptor.getValue();
        assertThat(actualInputText).startsWith(inputText);
        assertThat(actualInputObjArr).hasSize(1)
                .containsOnly(inputObject);
    }
}