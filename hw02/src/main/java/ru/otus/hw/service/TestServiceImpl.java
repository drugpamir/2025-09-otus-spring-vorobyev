package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    private final AppProperties appProperties;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");

        var questions = questionDao.findAll();
        var testQuestions = getRandomQuestions(questions, appProperties.getRightAnswersCountToPass());

        var testResult = new TestResult(student);

        for (var question : testQuestions) {
            printQuestion(question);
            printAnswers(question);

            int answer = readUserAnswer(question);

            handleUserAnswer(question, answer, testResult);
        }
        return testResult;
    }

    private List<Question> getRandomQuestions(List<Question> questions, int count) {
        return new Random().ints(0, questions.size()).boxed()
                .distinct()
                .limit(count)
                .map(questions::get)
                .toList();
    }

    private void printQuestion(Question question) {
        ioService.printLine("");
        ioService.printLine(question.text());
    }

    private void printAnswers(Question question) {
        var answersBuilder = new StringBuilder();
        for (int i = 0; i < question.answers().size(); i++) {
            answersBuilder.append(String.format("%d: %s\n", i + 1, question.answers().get(i).text()));
        }
        ioService.printLine(answersBuilder.toString());
    }

    private int readUserAnswer(Question question) {
        return ioService.readIntForRangeWithPrompt(
                1,
                question.answers().size(),
                String.format("Enter an answer from 1 to %d", question.answers().size()),
                "Answer input error"
        );
    }

    private static void handleUserAnswer(Question question, int answer, TestResult testResult) {
        var isAnswerValid = question.answers().get(answer - 1).isCorrect();
        testResult.applyAnswer(question, isAnswerValid);
    }
}

