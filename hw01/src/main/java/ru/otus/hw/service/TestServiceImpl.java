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
        List<Question> questions = getRandomQuestions(questionDao.findAll(), QUESTIONS_COUNT);
        StringBuilder formattedQuestionsWithAnswers = formatQuestionsWithAnswers(questions);
        printQuestionsWithAnswers(formattedQuestionsWithAnswers);
    }

    private StringBuilder formatQuestionsWithAnswers(List<Question> questions) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nPlease answer the questions below\n");
        questions.stream()
                .map(this::formatQuestionWithAnswers)
                .forEach(sb::append);
        return sb;
    }

    private StringBuilder formatQuestionWithAnswers(Question question) {
        StringBuilder sb = new StringBuilder(question.text());
        sb.append("\n");
        for (int i = 0; i < question.answers().size(); i++) {
            var answer = question.answers().get(i);
            sb.append(String.format("#%d: %s%n", i + 1, answer.text()));
        }
        sb.append("\n");
        return sb;
    }

    private void printQuestionsWithAnswers(StringBuilder sb) {
        ioService.printLine(sb.toString());
    }

    private List<Question> getRandomQuestions(List<Question> questions, int count) {
        return new Random().ints(0, questions.size()).boxed()
                .distinct()
                .limit(count)
                .map(questions::get)
                .toList();
    }
}
