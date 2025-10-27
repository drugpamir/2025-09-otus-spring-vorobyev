package ru.otus.hw.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class CsvQuestionDaoTest {

    private TestFileNameProvider fileNameProvider;

    private CsvQuestionDao csvQuestionDao;

    @BeforeEach
    void setUp() {
        fileNameProvider = mock(TestFileNameProvider.class);
        given(fileNameProvider.getTestFileName()).willReturn("/test_questions.csv");
        csvQuestionDao = new CsvQuestionDao(fileNameProvider);
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
    void shouldReturnListOfQuestions() {
        List<Question> questions = csvQuestionDao.findAll();
        assertThat(questions.size()).isEqualTo(5);
    }

    @Test
    void shouldNotContainComments() {
        List<Question> questions = csvQuestionDao.findAll();
        assertThat(questions.stream().noneMatch(q -> q.text().startsWith("#"))).isTrue();
    }
}