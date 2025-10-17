package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class TestServiceImplTest {

    private List<Question> questions;

    private IOService ioService;

    private QuestionDao questionDao;

    private TestServiceImpl testService;

    @BeforeEach
    void setUp() {
        questions = List.of(
                new Question("Question 1", List.of(new Answer("Answer 1", false), new Answer("Answer 2", true))),
                new Question("Question 2", List.of(new Answer("Answer 1", true), new Answer("Answer 2", false), new Answer("Answer 3", false))),
                new Question("Question 3", List.of(new Answer("Answer 1", false), new Answer("Answer 2", true), new Answer("Answer 3", false))),
                new Question("Question 4", List.of(new Answer("Answer 1", false), new Answer("Answer 2", false), new Answer("Answer 3", true))),
                new Question("Question 5", List.of(new Answer("Answer 1", false), new Answer("Answer 2", true), new Answer("Answer 3", true)))
        );

        ioService = mock(IOService.class);
        given(ioService.readLine()).willReturn("1").willReturn("2").willReturn("3").willReturn("4");

        questionDao = mock(QuestionDao.class);
        given(questionDao.findAll()).willReturn(questions);

        testService = new TestServiceImpl(ioService, questionDao);
//        given(testService.getUserAnswerNumber())
    }

    @Test
    void executeTest() {
        testService.executeTest();
        verify(questionDao, times(1)).findAll();
    }
}