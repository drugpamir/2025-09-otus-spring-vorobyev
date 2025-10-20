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

    private IOService ioService;

    private QuestionDao questionDao;

    private TestServiceImpl testService;

    @BeforeEach
    void setUp() {
        List<Question> questions = List.of(
                new Question("Question 1", List.of(new Answer("Answer 1", false), new Answer("Answer 2", true))),
                new Question("Question 2", List.of(new Answer("Answer 1", true), new Answer("Answer 2", false), new Answer("Answer 3", false))),
                new Question("Question 3", List.of(new Answer("Answer 1", false), new Answer("Answer 2", true), new Answer("Answer 3", false))),
                new Question("Question 4", List.of(new Answer("Answer 1", false), new Answer("Answer 2", false), new Answer("Answer 3", true))),
                new Question("Question 5", List.of(new Answer("Answer 1", false), new Answer("Answer 2", true), new Answer("Answer 3", true)))
        );

        ioService = mock(IOService.class);

        questionDao = mock(QuestionDao.class);
        given(questionDao.findAll()).willReturn(questions);

        testService = new TestServiceImpl(ioService, questionDao);
    }

    @Test
    void executeTest() {
        testService.executeTest();
        verify(questionDao, times(1)).findAll();
        verify(ioService).printLine(contains("Question"));
        verify(ioService).printLine(contains("Answer 1"));
        verify(ioService).printLine(contains("Answer 2"));
        verify(ioService).printLine(contains("Answer 3"));
    }
}