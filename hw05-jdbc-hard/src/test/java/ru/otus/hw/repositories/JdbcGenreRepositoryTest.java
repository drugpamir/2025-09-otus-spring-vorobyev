package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jdbc для работы с жанрами книг")
@JdbcTest
@Import(JdbcGenreRepository.class)
class JdbcGenreRepositoryTest {

    @Autowired
    private JdbcGenreRepository repositoryJdbc;

    @DisplayName("Должен загружать список всех жанров")
    @Test
    void shouldReturnCorrectGenresList() {
        var expectedGenres = getDbGenres();
        var actualGenres = repositoryJdbc.findAll();
        assertThat(actualGenres).containsExactlyElementsOf(expectedGenres);
    }

    @DisplayName("Должен загружать список жанров по набору id")
    @ParameterizedTest
    @CsvSource({"1,2", "2,3", "3,5", "1,6", "4,1"})
    void shouldReturnCorrectGenresListByIds(long id1, long id2) {
        var idsSet = Set.of(id1, id2);
        var expectedGenres = getDbGenres().stream()
                .filter(genre -> idsSet.contains(genre.getId()))
                .toList();
        var actualGenres = repositoryJdbc.findAllByIds(idsSet);
        assertThat(actualGenres).containsExactlyInAnyOrderElementsOf(expectedGenres);
    }

    private static List<Genre> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new Genre(id, "Genre_" + id))
                .toList();
    }
}