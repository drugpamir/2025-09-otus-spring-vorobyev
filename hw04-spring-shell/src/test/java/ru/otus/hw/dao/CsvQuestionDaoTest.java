package ru.otus.hw.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@ContextConfiguration(classes = CsvQuestionDao.class)
class CsvQuestionDaoTest {

    @MockitoBean
    private TestFileNameProvider fileNameProvider;

    @Autowired
    private CsvQuestionDao csvQuestionDao;

    @BeforeEach
    void setUp() {
        given(fileNameProvider.getTestFileName()).willReturn("/test_questions.csv");
    }

    @Test
    void shouldThrowCustomExceptionIfFileIsNotFound() {
        given(fileNameProvider.getTestFileName()).willReturn("/test_questions_not_found.csv");
        assertThatThrownBy(() -> csvQuestionDao.findAll()).isInstanceOf(QuestionReadException.class);
    }

    @Test
    void shouldThrowCustomExceptionIfFileIsBroken() {
        given(fileNameProvider.getTestFileName()).willReturn("/test_questions_broken.csv");
        assertThatThrownBy(() -> csvQuestionDao.findAll()).isInstanceOf(QuestionReadException.class);
    }

    @Test
    void shouldReturnExpectedCountOfQuestions() {
        List<Question> questions = csvQuestionDao.findAll();
        assertThat(questions.size()).isEqualTo(5);
    }

    @Test
    void shouldReturnCorrectQuestionsDate() {
        List<Question> expected = List.of(
                new Question("Question 1", List.of(new Answer("Answer 1", false), new Answer("Answer 2", true))),
                new Question("Question 2", List.of(new Answer("Answer 1", true), new Answer("Answer 2", false))),
                new Question("Question 3", List.of(new Answer("Answer 1", false), new Answer("Answer 2", true), new Answer("Answer 3", false))),
                new Question("Question 4", List.of(new Answer("Answer 1", false), new Answer("Answer 2", false), new Answer("Answer 3", true))),
                new Question("Question 5", List.of(new Answer("Answer 1", false), new Answer("Answer 2", true), new Answer("Answer 3", true)))
        );
        List<Question> actual = csvQuestionDao.findAll();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldNotContainComments() {
        List<Question> questions = csvQuestionDao.findAll();
        assertThat(questions.stream().noneMatch(q -> q.text().startsWith("#"))).isTrue();
    }
}