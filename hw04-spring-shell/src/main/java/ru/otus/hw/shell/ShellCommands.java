package ru.otus.hw.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.otus.hw.domain.Student;
import ru.otus.hw.service.ResultService;
import ru.otus.hw.service.TestRunnerService;
import ru.otus.hw.service.TestService;

@RequiredArgsConstructor
@ShellComponent
public class ShellCommands {

    private final TestService testService;

    private final TestRunnerService testRunnerService;

    private final ResultService resultService;

    private Student student;

    @ShellMethod(
            key = { "run", "run-app" },
            value = "Run the test command"
    )
    public void runApp() {
        testRunnerService.run();
    }

    @ShellMethod(
            key = { "login" },
            value = "Set the student's first and last names"
    )
    public String login(
            @ShellOption(help = "First Name") String firstName,
            @ShellOption(help = "Last Name") String lastName
    ) {
        student = new Student(firstName, lastName);
        return String.format("Successfully logged in as %s %s.%nTo start the testing input 'test' command.",
                firstName, lastName);
    }

    @ShellMethod(
            key = { "test", "run-test" },
            value = "Run the test for logged in user (by the 'login' command)"
    )
    public String testStudent() {
        if (student == null) {
            return "Please use 'login' command to set the user";
        }
        var testResult = testService.executeTestFor(student);
        resultService.showResult(testResult);
        return "Test is finished.";
    }
}
