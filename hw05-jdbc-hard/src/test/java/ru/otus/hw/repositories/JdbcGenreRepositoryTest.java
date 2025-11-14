package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
    @MethodSource("getGenresIds")
    void shouldReturnCorrectGenresListByIds(Set<Long> idsSet) {
        var expectedGenres = getDbGenres().stream()
                .filter(genre -> idsSet.contains(genre.getId()))
                .toList();
        var actualGenres = repositoryJdbc.findAllByIds(idsSet);
        assertThat(actualGenres).containsExactlyInAnyOrderElementsOf(expectedGenres);
    }

    public static Stream<Arguments> getGenresIds() {
        return Stream.of(Arguments.of(Set.of(1L, 2L)), Arguments.of(Set.of(2L, 3L)), Arguments.of(Set.of(3L, 5L)),
                Arguments.of(Set.of(1L, 6L)), Arguments.of(Set.of(4L, 1L)));
    }

    private static List<Genre> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new Genre(id, "Genre_" + id))
                .toList();
    }
}