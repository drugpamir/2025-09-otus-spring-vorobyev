package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    private static final int QUESTIONS_COUNT = 3;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");

        int rightAnswersCount = 0;
        var scanner = new Scanner(System.in);
        var questions = getRandomQuestions(questionDao.findAll(), QUESTIONS_COUNT);
        for (var question : questions) {
            ioService.printLine(question.text());
            for (int i = 0; i < question.answers().size(); i++) {
                var answer = question.answers().get(i);
                ioService.printFormattedLine("#%d: %s", i + 1, answer.text());
            }
            ioService.printLine("\nEnter the correct answer number:");
            int answerIndex = scanner.nextInt() - 1;
            if (question.answers().get(answerIndex).isCorrect()) {
                rightAnswersCount++;
            }
            ioService.printLine("");
        }

        ioService.printFormattedLine("\nВаш результат: %d из %d правильных ответов", rightAnswersCount, QUESTIONS_COUNT);
    }

    private List<Question> getRandomQuestions(List<Question> questions, int count) {
        return new Random().ints(0, questions.size()).boxed()
                .distinct()
                .limit(count)
                .map(questions::get)
                .toList();
    }
}
