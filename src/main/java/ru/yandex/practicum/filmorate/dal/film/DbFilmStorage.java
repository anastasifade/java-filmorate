package ru.yandex.practicum.filmorate.dal.film;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.DbStorage;
import ru.yandex.practicum.filmorate.enums.SearchParam;
import ru.yandex.practicum.filmorate.enums.SortParam;
import ru.yandex.practicum.filmorate.exceptions.FailedToDeleteException;
import ru.yandex.practicum.filmorate.exceptions.InternalServerException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Repository
@Primary
@Transactional
public class DbFilmStorage extends DbStorage<Film> implements FilmStorage {
    private static final String FIND_ALL = """
            SELECT f.*,
                   m.name AS mpa_name,
                   g.id AS genre_id,
                   g.name AS genre_name,
                   d.id AS director_id,
                   d.name AS director_name
            FROM films AS f
            JOIN mpa AS m ON m.id = f.mpa_id
            LEFT JOIN films_genres AS fg ON fg.film_id = f.id
            LEFT JOIN genres AS g ON g.id = fg.genre_id
            LEFT JOIN films_directors AS fd ON fd.film_id = f.id
            LEFT JOIN directors AS d ON d.id = fd.director_id
            ORDER BY f.id, g.id, d.id
            """;

    private static final String FIND_BY_ID = """
            SELECT f.*,
                   m.name AS mpa_name,
                   g.id AS genre_id,
                   g.name AS genre_name,
                   d.id AS director_id,
                   d.name AS director_name
            FROM films AS f
            JOIN mpa AS m ON m.id = f.mpa_id
            LEFT JOIN films_genres AS fg ON fg.film_id = f.id
            LEFT JOIN genres AS g ON g.id = fg.genre_id
            LEFT JOIN films_directors AS fd ON fd.film_id = f.id
            LEFT JOIN directors AS d ON d.id = fd.director_id
            WHERE f.id = ?
            ORDER BY f.id, g.id, d.id
            """;

    private static final String FIND_POPULAR = """
            WITH
            popular AS (SELECT f.id, COUNT(fl.user_id) AS likes
                        FROM films f
                        LEFT JOIN films_likes fl ON fl.film_id = f.id
                        GROUP BY f.id
                        ORDER BY likes DESC, f.id
                        LIMIT ?)
            SELECT f.*,
                   m.name AS mpa_name,
                   g.id AS genre_id,
                   g.name AS genre_name,
                   d.id AS director_id,
                   d.name AS director_name,
                   p.likes AS likes
                   FROM popular p
                   LEFT JOIN films AS f ON f.id = p.id
                   LEFT JOIN films_genres AS fg ON fg.film_id = f.id
                   LEFT JOIN genres AS g ON g.id = fg.genre_id
                   LEFT JOIN mpa AS m ON m.id = f.mpa_id
                   LEFT JOIN films_directors AS fd ON fd.film_id = f.id
                   LEFT JOIN directors AS d ON d.id = fd.director_id
            """;

    private static final String FIND_RECOMMENDED_FILMS = """
            WITH
            user_likes AS (
                SELECT film_id
                FROM films_likes
                WHERE user_id = ?),
            similar_user AS (
                SELECT fl.user_id
                FROM films_likes fl
                JOIN user_likes ul ON fl.film_id = ul.film_id
                WHERE fl.user_id != ?
                GROUP BY fl.user_id
                ORDER BY COUNT(fl.film_id) DESC
                LIMIT 1
            ),
            recommended AS (
                SELECT DISTINCT fl.film_id
                FROM films_likes fl
                JOIN similar_user su ON fl.user_id = su.user_id
                WHERE fl.film_id NOT IN (SELECT film_id FROM user_likes)
            )
            SELECT f.*,
                   m.name AS mpa_name,
                   g.id AS genre_id,
                   g.name AS genre_name,
                   d.id AS director_id,
                   d.name AS director_name
            FROM films f
            LEFT JOIN films_genres fg ON fg.film_id = f.id
            LEFT JOIN genres g ON g.id = fg.genre_id
            LEFT JOIN mpa m ON m.id = f.mpa_id
            LEFT JOIN films_directors fd ON fd.film_id = f.id
            LEFT JOIN directors d ON d.id = fd.director_id
            JOIN recommended r ON f.id = r.film_id
            ORDER BY f.id;
            """;

    private static final String FIND_BY_DIRECTOR_SORT_LIKES = """
            WITH
            film_likes AS (
                SELECT film_id, COUNT(user_id) AS likes
                FROM films_likes
                GROUP BY film_id)
            SELECT f.*,
                   m.name AS mpa_name,
                   g.id   AS genre_id,
                   g.name AS genre_name,
                   d.id   AS director_id,
                   d.name AS director_name
            FROM films_directors fd
            JOIN films f ON f.id = fd.film_id
            LEFT JOIN film_likes fl ON fl.film_id = f.id
            LEFT JOIN films_genres fg ON fg.film_id = f.id
            LEFT JOIN genres g ON g.id = fg.genre_id
            LEFT JOIN mpa m ON m.id = f.mpa_id
            LEFT JOIN directors d ON d.id = fd.director_id
            WHERE fd.director_id = ?
            ORDER BY COALESCE(fl.likes, 0) DESC, f.id
            """;

    private static final String FIND_BY_DIRECTOR_SORT_YEAR = """
            SELECT f.*,
                   m.name AS mpa_name,
                   g.id AS genre_id,
                   g.name AS genre_name,
                   d.id AS director_id,
                   d.name AS director_name
            FROM films AS f
            JOIN mpa AS m ON m.id = f.mpa_id
            LEFT JOIN films_genres AS fg ON fg.film_id = f.id
            LEFT JOIN genres AS g ON g.id = fg.genre_id
            LEFT JOIN films_directors AS fd ON fd.film_id = f.id
            LEFT JOIN directors AS d ON d.id = fd.director_id
            WHERE d.id = ?
            ORDER BY f.release_date, f.id
            """;

    private static final String INSERT_FILM = """
            INSERT INTO films (name, release_date, description, duration, mpa_id)
            VALUES(?, ?, ?, ?, ?)
            """;

    private static final String INSERT_FILMS_GENRES = """
            INSERT INTO films_genres (film_id, genre_id)
            VALUES (?, ?)
            """;

    private static final String INSERT_FILMS_LIKES = """
            MERGE INTO films_likes (film_id, user_id)
            VALUES (?, ?)
            """;

    private static final String INSERT_FILMS_DIRECTORS = """
            INSERT INTO films_directors (film_id, director_id)
            VALUES (?, ?)
            """;

    private static final String UPDATE_FILMS = """
            UPDATE films
            SET name = ?, release_date = ?, description = ?, duration = ?, mpa_id = ?
            WHERE id = ?
            """;

    private static final String DELETE_FILMS_GENRES = """
            DELETE FROM films_genres
            WHERE film_id = ?
            """;

    private static final String DELETE_FILMS_LIKES = """
            DELETE FROM films_likes
            WHERE film_id = ? AND user_id = ?
            """;

    private static final String DELETE_FILMS_DIRECTORS = """
            DELETE FROM films_directors
            WHERE film_id = ?
            """;

    private static final String FIND_FILMS_BY_PARAMS = """
            SELECT f.*,
                   m.name AS mpa_name,
                   g.id AS genre_id,
                   g.name AS genre_name,
                   d.id AS director_id,
                   d.name AS director_name
            FROM films AS f
            JOIN mpa AS m ON m.id = f.mpa_id
            LEFT JOIN films_genres AS fg ON fg.film_id = f.id
            LEFT JOIN genres AS g ON g.id = fg.genre_id
            LEFT JOIN films_directors AS fd ON fd.film_id = f.id
            LEFT JOIN directors AS d ON d.id = fd.director_id
            LEFT JOIN (
                SELECT film_id, COUNT(user_id) AS likes_count
                FROM films_likes
                GROUP BY film_id
            ) AS film_likes ON film_likes.film_id = f.id
            """;

    private static final String FIND_COMMON_FILMS = """
            WITH
            common AS (SELECT fl1.film_id
                       FROM films_likes fl1
                       JOIN films_likes fl2 ON fl1.film_id = fl2.film_id
                       WHERE fl1.user_id = ? AND fl2.user_id = ?),
            popular AS (SELECT f.id, COUNT(fl.user_id) AS likes
                        FROM common c
                        LEFT JOIN films f ON f.id = c.film_id
                        LEFT JOIN films_likes fl ON fl.film_id = f.id
                        GROUP BY f.id),
            sel AS (SELECT f.*,
                    m.name AS mpa_name,
                    g.id AS genre_id,
                    g.name AS genre_name,
                    d.id AS director_id,
                    d.name AS director_name,
                    p.likes AS likes
                    FROM popular p
                    LEFT JOIN films AS f ON f.id = p.id
                    LEFT JOIN films_genres AS fg ON fg.film_id = f.id
                    LEFT JOIN genres AS g ON g.id = fg.genre_id
                    LEFT JOIN mpa AS m ON m.id = f.mpa_id
                    LEFT JOIN films_directors AS fd ON fd.film_id = f.id
                    LEFT JOIN directors AS d ON d.id = fd.director_id)
            SELECT *
            FROM   sel
            ORDER  BY likes DESC, id
            """;

    private final ResultSetExtractor<List<Film>> extractor;

    public DbFilmStorage(JdbcTemplate jdbc, ResultSetExtractor<List<Film>> extractor) {
        super("films", jdbc, null);
        this.extractor = extractor;
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<Film> findAll() {
        return findMany(FIND_ALL, extractor);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Film> findById(long id) {
        try {
            List<Film> result = jdbc.query(FIND_BY_ID, extractor, id);
            if (result == null || result.isEmpty()) {
                return Optional.empty();
            }
            return Optional.ofNullable(result.getFirst());
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<Film> findPopular(int count, Long genreId, Integer year) {
        StringBuilder sql = new StringBuilder(FIND_POPULAR);
        List<Object> params = new ArrayList<>();

        params.add(count);

        if (genreId != null || year != null) {
            sql.append(" WHERE ");
            List<String> conditions = new ArrayList<>();

            if (genreId != null) {
                conditions.add("f.id IN (SELECT film_id FROM films_genres WHERE genre_id = ?)");
                params.add(genreId);
            }
            if (year != null) {
                conditions.add("YEAR(f.release_date) = ?");
                params.add(year);
            }

            sql.append(String.join(" AND ", conditions));
        }
        sql.append(" ORDER BY likes DESC, id");
        return findMany(sql.toString(), extractor, params.toArray());
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<Film> findCommonFilms(Long userId, Long friendId) {
        return findMany(FIND_COMMON_FILMS, extractor, userId, friendId);
    }

    @Transactional(readOnly = true)
    public Collection<Film> findByDirectorSorted(Long directorId, SortParam sortBy) {
        String query = sortBy.equals(SortParam.YEAR) ? FIND_BY_DIRECTOR_SORT_YEAR : FIND_BY_DIRECTOR_SORT_LIKES;
        return findMany(query, extractor, directorId);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<Film> searchFilmsByParams(String query, Set<SearchParam> searchParams) {
        List<String> conditions = new ArrayList<>();
        List<String> params = new ArrayList<>();

        if (searchParams.contains(SearchParam.TITLE)) {
            conditions.add("LOWER(f.name) LIKE ?");
            params.add("%" + query.toLowerCase() + "%");
        }
        if (searchParams.contains(SearchParam.DIRECTOR)) {
            conditions.add("LOWER(d.name) LIKE ?");
            params.add("%" + query.toLowerCase() + "%");
        }

        if (conditions.isEmpty())
            return List.of();

        String sql = FIND_FILMS_BY_PARAMS +
                "WHERE " + String.join(" OR ", conditions) + " " +
                "ORDER BY film_likes.likes_count DESC, f.id";

        return findMany(sql, extractor, params.toArray());
    }

    @Override
    public Film create(Film obj) {
        Long id = insert(INSERT_FILM,
                obj.getName(),
                Date.valueOf(obj.getReleaseDate()),
                obj.getDescription(),
                obj.getDuration(),
                obj.getMpa().getId());

        if (obj.getGenres() != null && !(obj.getGenres().isEmpty())) {
            int[] rows = batchUpdate(INSERT_FILMS_GENRES,
                    getBatchPsSetterForFilmsGenres(id, obj.getGenres().stream().toList()));
            if (rows.length != obj.getGenres().size()) {
                log.debug("Failed to insert genres when creating film: {}.", obj);
                throw new InternalServerException("Failed to add genres when creating film.");
            }
        }

        if (obj.getDirectors() != null && !(obj.getDirectors().isEmpty())) {
            int[] rows = batchUpdate(INSERT_FILMS_DIRECTORS,
                    getBatchPsSetterForFilmsDirectors(id, obj.getDirectors().stream().toList()));
            if (rows.length != obj.getDirectors().size()) {
                log.debug("Failed to insert directors when creating film: {}.", obj);
                throw new InternalServerException("Failed to add directors when creating film.");
            }
        }

        Optional<Film> film = findById(id);
        return film.orElseThrow(() -> new InternalServerException("Failed to create film."));
    }

    @Override
    public Film update(Film obj) {
        log.trace("Update request for film: {}.", obj);
        Long id = obj.getId();

        update(UPDATE_FILMS,
                obj.getName(),
                Date.valueOf(obj.getReleaseDate()),
                obj.getDescription(),
                obj.getDuration(),
                obj.getMpa().getId(),
                id);

        delete(DELETE_FILMS_GENRES, id);
        delete(DELETE_FILMS_DIRECTORS, id);

        if (obj.getGenres() != null) {
            int[] rows = batchUpdate(INSERT_FILMS_GENRES, getBatchPsSetterForFilmsGenres(id, obj.getGenres().stream().toList()));
            if (rows.length != obj.getGenres().size()) {
                log.debug("Failed to insert new genres:[{}] when updating film: {}.", obj.getGenres(), obj);
                throw new InternalServerException("Failed to update genres when updating film.");
            }
        }

        if (obj.getDirectors() != null) {
            int[] rows = batchUpdate(INSERT_FILMS_DIRECTORS,
                    getBatchPsSetterForFilmsDirectors(id, obj.getDirectors().stream().toList()));
            if (rows.length != obj.getDirectors().size()) {
                log.debug("Failed to insert new directors:[{}] when updating film: {}.", obj.getGenres(), obj);
                throw new InternalServerException("Failed to update directors when updating film.");
            }
        }

        Optional<Film> film = findById(id);
        return film.orElseThrow(() -> new InternalServerException("Failed to update film."));
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        update(INSERT_FILMS_LIKES, filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        try {
            delete(DELETE_FILMS_LIKES, filmId, userId);
        } catch (FailedToDeleteException e) {
            log.trace("Failed to delete like by user [id={}] for film [id={}].", userId, filmId);
        }
    }

    @Transactional(readOnly = true)
    public Collection<Film> recommend(Long userId) {
        return findMany(FIND_RECOMMENDED_FILMS, extractor, userId, userId);
    }

    private BatchPreparedStatementSetter getBatchPsSetterForFilmsGenres(Long filmId, List<Genre> genres) {
        return new BatchPreparedStatementSetter() {
            @Override
            public void setValues(@NonNull PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId);
                ps.setLong(2, genres.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        };
    }

    private BatchPreparedStatementSetter getBatchPsSetterForFilmsDirectors(Long filmId,
                                                                           List<Director> directors) {
        return new BatchPreparedStatementSetter() {
            @Override
            public void setValues(@NonNull PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId);
                ps.setLong(2, directors.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return directors.size();
            }
        };
    }
}
