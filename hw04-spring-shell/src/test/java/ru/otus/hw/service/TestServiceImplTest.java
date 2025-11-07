package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = TestServiceImpl.class)
class TestServiceImplTest {

    @MockitoBean
    private LocalizedIOService ioService;

    @MockitoBean
    private QuestionDao questionDao;

    @MockitoBean
    private AppProperties appProperties;

    @Autowired
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

        given(ioService.readIntForRangeWithPrompt(anyInt(), anyInt(), anyString(), anyString())).willReturn(1);
        given(ioService.getMessage(anyString(), anyInt())).willReturn("Prompt");
        given(ioService.getMessage(anyString())).willReturn("Prompt");

        given(questionDao.findAll()).willReturn(questions);
    }

    @Test
    void shouldFetchQuestionsFromDao() {
        testService.executeTestFor(new Student("John", "Doe"));
        verify(questionDao, times(1)).findAll();
    }

    @Test
    void shouldPrintQuestionTextToUser() {
        testService.executeTestFor(new Student("John", "Doe"));
        verify(ioService, atLeast(appProperties.getRightAnswersCountToPass())).printLine(anyString());
    }
}