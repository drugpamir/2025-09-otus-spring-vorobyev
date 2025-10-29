package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.config.TestConfig;
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

    private final TestConfig testConfig;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printFormattedLine("%nPlease answer the questions below%n");

        var questions = questionDao.findAll();
        var testQuestions = getRandomQuestions(questions, testConfig.getAskingQuestionsCount());

        var testResult = new TestResult(student);

        for (var question : testQuestions) {
            var questionText = getQuestionText(question);
            printQuestion(questionText);

            int answer = readUserAnswer(question);
            var isAnswerValid = askQuestion(question, answer);

            testResult.applyAnswer(question, isAnswerValid);
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

    private String getQuestionText(Question question) {
        var answersBuilder = new StringBuilder(System.lineSeparator())
                .append(question.text())
                .append(System.lineSeparator());
        for (int i = 0; i < question.answers().size(); i++) {
            answersBuilder.append(String.format("%d: %s\n", i + 1, question.answers().get(i).text()));
        }
        return answersBuilder.toString();
    }

    private void printQuestion(String questionText) {
        ioService.printLine(questionText);
    }

    private int readUserAnswer(Question question) {
        return ioService.readIntForRangeWithPrompt(
                1,
                question.answers().size(),
                String.format("Enter an answer from 1 to %d", question.answers().size()),
                "Answer input error"
        );
    }

    private boolean askQuestion(Question question, int answer) {
        return question.answers().get(answer - 1).isCorrect();
    }
}

