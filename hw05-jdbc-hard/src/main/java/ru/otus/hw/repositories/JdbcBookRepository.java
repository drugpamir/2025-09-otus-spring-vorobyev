package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final NamedParameterJdbcTemplate jdbc;

    private final GenreRepository genreRepository;

    @Override
    public Optional<Book> findById(long id) {
        var sql = """
                select b.id, b.title, a.id as a_id, a.full_name as a_full_name\s
                from books b\s
                    left join authors a\s
                        on a.id = b.author_id
                where b.id = :id
               \s""";
        var params = Map.of("id", id);
        var book = jdbc.query(sql, params, new BookResultSetExtractor());
        if (book == null) {
            return Optional.empty();
        }
        var relations = getGenreRelationsByBookId(id);
        var genreIds = relations.stream().map(BookGenreRelation::genreId).collect(Collectors.toSet());
        var genres = genreRepository.findAllByIds(genreIds);
        mergeBooksInfo(List.of(book), genres, relations);
        return Optional.of(book);
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
        var params = Map.of("id", id);
        jdbc.update("delete from books where id = :id", params);
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

    private List<BookGenreRelation> getGenreRelationsByBookId(long id) {
        var sql = "select book_id, genre_id from books_genres where book_id = :id";
        var params = Map.of("id", id);
        return jdbc.query(sql, params, new BookGenreRelationMapper());
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        var booksMap = booksWithoutGenres.stream()
                .collect(Collectors.toMap(Book::getId, Function.identity()));
        var genresMap = genres.stream()
                .collect(Collectors.toMap(Genre::getId, Function.identity()));
        relations.forEach(relation -> {
            var book = booksMap.get(relation.bookId);
            var genre = genresMap.get(relation.genreId);
            book.getGenres().add(genre);
        });
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();
        var params = new MapSqlParameterSource()
                .addValue("id", keyHolder)
                .addValue("title", book.getTitle())
                .addValue("author_id", book.getAuthor().getId());
        var sql = "insert into books (title, author_id) values (:title, :author_id)";
        jdbc.update(sql, params, keyHolder);

        //noinspection DataFlowIssue
        book.setId(keyHolder.getKeyAs(Long.class));
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        var params = Map.of(
                "id", book.getId(),
                "title", book.getTitle(),
                "author_id", book.getAuthor().getId()
        );
        var sql = "update books set title = :title, author_id = :author_id where id = :id";
        int updatedRowsCount = jdbc.update(sql, params);
        if (updatedRowsCount == 0) {
            var message = String.format("Book.id = %d not found. Fields are not updated.", book.getId());
            throw new EntityNotFoundException(message);
        }

        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        var params = book.getGenres().stream()
                .map(genre ->
                        new MapSqlParameterSource()
                                .addValue("book_id", book.getId())
                                .addValue("genre_id", genre.getId()))
                .toArray(MapSqlParameterSource[]::new);
        var sql = "insert into books_genres (book_id, genre_id) values (:book_id, :genre_id)";
        jdbc.batchUpdate(sql, params);
    }

    private void removeGenresRelationsFor(Book book) {
        var params = Map.of("book_id", book.getId());
        jdbc.update("delete from books_genres where book_id = :book_id", params);
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

    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (!rs.next()) {
                return null;
            }
            var bookId = rs.getLong("id");
            var bookTitle = rs.getString("title");
            var author = new Author(rs.getLong("a_id"), rs.getString("a_full_name"));
            return new Book(bookId, bookTitle, author, new LinkedList<>());
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }
}
