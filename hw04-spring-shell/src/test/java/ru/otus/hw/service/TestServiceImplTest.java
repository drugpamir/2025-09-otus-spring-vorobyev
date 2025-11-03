package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class TestServiceImplTest {

    private LocalizedIOService ioService;

    private QuestionDao questionDao;

    private AppProperties appProperties;

    private Student student;

    private TestService testService;

    @BeforeEach
    void setUp() {
        List<Question> questions = List.of(
                new Question("Question 1", List.of(new Answer("Answer 1", false), new Answer("Answer 2", true))),
                new Question("Question 2", List.of(new Answer("Answer 1", true), new Answer("Answer 2", false), new Answer("Answer 3", false))),
                new Question("Question 3", List.of(new Answer("Answer 1", false), new Answer("Answer 2", true), new Answer("Answer 3", false))),
                new Question("Question 4", List.of(new Answer("Answer 1", false), new Answer("Answer 2", false), new Answer("Answer 3", true))),
                new Question("Question 5", List.of(new Answer("Answer 1", false), new Answer("Answer 2", true), new Answer("Answer 3", true)))
        );

        ioService = mock(LocalizedIOService.class);
        given(ioService.readIntForRangeWithPrompt(anyInt(), anyInt(), anyString(), anyString())).willReturn(1);

        appProperties = mock(AppProperties.class);
        given(appProperties.getRightAnswersCountToPass()).willReturn(3);

        questionDao = mock(QuestionDao.class);
        given(questionDao.findAll()).willReturn(questions);

        var studentService = mock(StudentService.class);
        given(studentService.determineCurrentStudent()).willReturn(new Student("John", "Doe"));

        student = studentService.determineCurrentStudent();
        testService = new TestServiceImpl(ioService, questionDao);
    }

    @Test
    void shouldFetchQuestionsFromDao() {
        testService.executeTestFor(student);
        verify(questionDao, times(1)).findAll();
    }

    @Test
    void shouldPrintQuestionTextToUser() {
        testService.executeTestFor(student);
        verify(ioService, atLeast(appProperties.getRightAnswersCountToPass())).printLine(anyString());
    }
}