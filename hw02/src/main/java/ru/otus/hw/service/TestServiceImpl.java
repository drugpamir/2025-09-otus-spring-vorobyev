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
            ioService.printLine(question.text());

            var answersBuilder = new StringBuilder();
            for (int i = 0; i < question.answers().size(); i++) {
                answersBuilder.append(String.format("%d: %s\n", i + 1, question.answers().get(i).text()));
            }

            int answer = ioService.readIntForRangeWithPrompt(1, question.answers().size(), answersBuilder.toString(), "Answer input error");

            var isAnswerValid = question.answers().get(answer - 1).isCorrect();
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
}

