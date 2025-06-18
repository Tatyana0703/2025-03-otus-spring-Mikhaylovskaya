package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.service.TestRunnerService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@RequiredArgsConstructor
@ShellComponent
public class AppCommands {

    private final TestRunnerService testRunnerService;

    @ShellMethod(value = "Start testing", key = "t")
    public void runTesting() {
        testRunnerService.run();
    }
}
