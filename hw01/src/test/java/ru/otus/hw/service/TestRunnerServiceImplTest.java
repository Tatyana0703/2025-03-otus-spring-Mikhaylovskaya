package ru.otus.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.exceptions.QuestionReadException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TestRunnerServiceImplTest {

    @Mock
    private TestService testService;
    @Mock
    private IOService ioService;
    @InjectMocks
    private TestRunnerServiceImpl runnerService;

    @DisplayName("должен вызывать сервис тестирования при валидном csv-файле")
    @Test
    void shouldCorrectRunTestService() {
        runnerService.run();
        verify(testService, times(1)).executeTest();
        verifyNoMoreInteractions(testService);
        verifyNoInteractions(ioService);
    }

    @DisplayName("должен корректно обрабатывать ошибку от сервиса тестирования")
    @Test
    void shouldHandleExceptionFromTestService() {
        willThrow(new QuestionReadException("")).given(testService).executeTest();
        runnerService.run();
        verify(testService, times(1)).executeTest();
        verify(ioService, times(1)).printLine(any());
    }
}