package ru.otus.hw.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.service.TestRunnerService;

@RequiredArgsConstructor
@ShellComponent
public class ShellCommands {

    private final TestRunnerService testRunnerService;

    @ShellMethod(
            key = { "run", "run-app" },
            value = "Run the test command"
    )
    public void runApp() {
        testRunnerService.run();
    }
}
