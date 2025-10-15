package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
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

        int rightAnswersCount = 0;
        var questions = getRandomQuestions(questionDao.findAll(), QUESTIONS_COUNT);
        for (var question : questions) {
            printQuestionWithAnswers(question);
            int answerNum = getUserAnswerNumber(question);
            if (isAnswerRight(question, answerNum)) {
                rightAnswersCount++;
            }
        }

        ioService.printFormattedLine("%nВаш результат: %d из %d правильных ответов", rightAnswersCount, QUESTIONS_COUNT);
    }

    private void printQuestionWithAnswers(Question question) {
        ioService.printLine(question.text());
        for (int i = 0; i < question.answers().size(); i++) {
            var answer = question.answers().get(i);
            ioService.printFormattedLine("#%d: %s", i + 1, answer.text());
        }
    }

    protected int getUserAnswerNumber(Question question) {
        ioService.printLine("\nEnter the correct answer number:");
        while (true) {
            String answerStr = ioService.readLine();
            int answerNum;
            if (!NumberUtils.isCreatable(answerStr)
                    || (answerNum = Integer.parseInt(answerStr)) < 1
                    || answerNum > question.answers().size()
            ) {
                ioService.printFormattedLine("Invalid answer: '%s'. Values should be a number in range %d..%d",
                        answerStr, 1, question.answers().size());
                continue;
            }
            ioService.printLine("");
            return answerNum;
        }
    }

    protected boolean isAnswerRight(Question question, int answerNum) {
        return question.answers().get(answerNum - 1).isCorrect();
    }

    protected List<Question> getRandomQuestions(List<Question> questions, int count) {
        return new Random().ints(0, questions.size()).boxed()
                .distinct()
                .limit(count)
                .map(questions::get)
                .toList();
    }
}
