package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final NamedParameterJdbcTemplate jdbc;

    private final GenreRepository genreRepository;

    @Override
    public Optional<Book> findById(long id) {
        return Optional.empty();
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var books = getAllBooksWithoutGenres();
        var relations = getAllGenreRelations();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        //...
    }

    private List<Book> getAllBooksWithoutGenres() {
        var sql = """
                select b.id, b.title, a.id as a_id, a.full_name as a_full_name\s
                from books b\s
                    left join authors a\s
                        on a.id = b.author_id
               \s""";
        return jdbc.getJdbcOperations().query(sql, new BookRowMapper());
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        var sql = "select book_id, genre_id from books_genres";
        return jdbc.getJdbcOperations().query(sql, new BookGenreRelationMapper());
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        relations.forEach(relation -> {
            Optional<Book> book = booksWithoutGenres.stream().filter(b -> b.getId() == relation.bookId).findFirst();
            Optional<Genre> genre = genres.stream().filter(g -> g.getId() == relation.genreId).findFirst();
            if (book.isPresent() && genre.isPresent()) {
                book.get().getGenres().add(genre.get());
            }
        });
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();

        //...

        //noinspection DataFlowIssue
        book.setId(keyHolder.getKeyAs(Long.class));
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        //...

        // Выбросить EntityNotFoundException если не обновлено ни одной записи в БД
        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        // Использовать метод batchUpdate
    }

    private void removeGenresRelationsFor(Book book) {
        //...
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            var bookId = rs.getLong("id");
            var bookTitle = rs.getString("title");
            var author = new Author(rs.getLong("a_id"), rs.getString("a_full_name"));
            return new Book(bookId, bookTitle, author, new LinkedList<>());
        }
    }

    private static class BookGenreRelationMapper implements RowMapper<BookGenreRelation> {

        @Override
        public BookGenreRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
            var bookId = rs.getLong("book_id");
            var genreId = rs.getLong("genre_id");
            return new BookGenreRelation(bookId, genreId);
        }
    }

    // Использовать для findById
    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            return null;
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }
}
