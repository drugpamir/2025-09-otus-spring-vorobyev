package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;

import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private static final int QUESTIONS_COUNT = 3;

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");

        var questions = getRandomQuestions(questionDao.findAll(), QUESTIONS_COUNT);
        for (var question : questions) {
            printQuestionWithAnswers(question);
        }
    }

    private void printQuestionWithAnswers(Question question) {
        ioService.printLine(question.text());
        for (int i = 0; i < question.answers().size(); i++) {
            var answer = question.answers().get(i);
            ioService.printFormattedLine("#%d: %s", i + 1, answer.text());
        }
        ioService.printLine("");
    }

    private List<Question> getRandomQuestions(List<Question> questions, int count) {
        return new Random().ints(0, questions.size()).boxed()
                .distinct()
                .limit(count)
                .map(questions::get)
                .toList();
    }
}
