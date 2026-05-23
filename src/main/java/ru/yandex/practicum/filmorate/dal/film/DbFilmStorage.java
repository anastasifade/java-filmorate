package ru.yandex.practicum.filmorate.dal.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.enums.SortParam;
import ru.yandex.practicum.filmorate.exceptions.FailedToDeleteException;
import ru.yandex.practicum.filmorate.exceptions.InternalServerException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dal.DbStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@Primary
@Transactional
public class DbFilmStorage extends DbStorage<Film> implements FilmStorage {

    private static final String FIND_ALL = """
            SELECT f.id AS id,
                   f.name AS name,
                   f.release_date AS release_date,
                   f.description AS description,
                   f.duration AS duration,
                   f.mpa_id AS mpa_id,
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
            SELECT f.id AS id,
                   f.name AS name,
                   f.release_date AS release_date,
                   f.description AS description,
                   f.duration AS duration,
                   f.mpa_id AS mpa_id,
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

    private static final String FIND_BY_NAME_RELEASE_DURATION = """
            SELECT f.id AS id,
                   f.name AS name,
                   f.release_date AS release_date,
                   f.description AS description,
                   f.duration AS duration,
                   f.mpa_id AS mpa_id,
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
            WHERE f.name = ? AND f.release_date = ? AND f.duration = ?
            GROUP BY f.id
            ORDER BY f.id, g.id, d.id
            """;

    private static final String FIND_POPULAR = """
            SELECT f.id AS id,
                   f.name AS name,
                   f.release_date AS release_date,
                   f.description AS description,
                   f.duration AS duration,
                   f.mpa_id AS mpa_id,
                   m.name AS mpa_name,
                   g.id AS genre_id,
                   g.name AS genre_name,
                   d.id AS director_id,
                   d.name AS director_name
            FROM (SELECT films.id AS id,
                         COUNT(fl.user_id) AS likes
                  FROM films
                  LEFT JOIN films_likes AS fl ON fl.film_id = films.id
                  GROUP BY films.id
                  ORDER BY likes DESC, films.id
                  LIMIT ?) popular
            LEFT JOIN films AS f ON f.id = popular.id
            LEFT JOIN films_genres AS fg ON fg.film_id = f.id
            LEFT JOIN genres AS g ON g.id = fg.genre_id
            LEFT JOIN mpa AS m ON m.id = f.mpa_id
            LEFT JOIN films_directors AS fd ON fd.film_id = f.id
            LEFT JOIN directors AS d ON d.id = fd.director_id
            """;

    private static final String FIND_BY_DIRECTOR_SORT_LIKES = """
            SELECT f.id AS id,
                   f.name AS name,
                   f.release_date AS release_date,
                   f.description AS description,
                   f.duration AS duration,
                   f.mpa_id AS mpa_id,
                   m.name AS mpa_name,
                   g.id AS genre_id,
                   g.name AS genre_name,
                   d.id AS director_id,
                   d.name AS director_name
            FROM (SELECT films.id AS id,
                         COUNT(fl.user_id) AS likes
                  FROM films
                  LEFT JOIN films_likes AS fl ON fl.film_id = films.id
                  GROUP BY films.id
                  ORDER BY likes DESC, films.id) popular
            LEFT JOIN films AS f ON f.id = popular.id
            LEFT JOIN films_genres AS fg ON fg.film_id = f.id
            LEFT JOIN genres AS g ON g.id = fg.genre_id
            LEFT JOIN mpa AS m ON m.id = f.mpa_id
            LEFT JOIN films_directors AS fd ON fd.film_id = f.id
            LEFT JOIN directors AS d ON d.id = fd.director_id
            WHERE d.id = ?
            ORDER BY popular.likes DESC, f.id
            """;

    private static final String FIND_BY_DIRECTOR_SORT_YEAR = """
            SELECT f.id AS id,
                   f.name AS name,
                   f.release_date AS release_date,
                   f.description AS description,
                   f.duration AS duration,
                   f.mpa_id AS mpa_id,
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
            INSERT INTO films_likes (film_id, user_id)
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
            if (result.isEmpty()) {
                return Optional.empty();
            }
            return Optional.ofNullable(result.get(0));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Film> findBy(String name, LocalDate releaseDate, int duration) {
        try {
            Collection<Film> result = findMany(FIND_BY_NAME_RELEASE_DURATION, extractor,
                    name, Date.valueOf(releaseDate), duration);
            return result.stream().findFirst();
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<Film> findPopular(int count) {
        return findMany(FIND_POPULAR + "ORDER BY popular.likes DESC, f.id;", extractor, count);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<Film> findPopular(int count, Long genreId, int year) {
        String sql = FIND_POPULAR + "WHERE fg.genre_id = ? AND YEAR(f.release_date) = ? " +
                "ORDER BY popular.likes DESC, f.id;";
        return findMany(sql, extractor, count, genreId, year);
    }

    @Transactional(readOnly = true)
    public Collection<Film> findByDirectorSorted(Long directorId, SortParam sortBy) {
        String query = sortBy.equals(SortParam.YEAR) ? FIND_BY_DIRECTOR_SORT_YEAR : FIND_BY_DIRECTOR_SORT_LIKES;
        return findMany(query, extractor, directorId);
    }

    @Transactional
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

    @Transactional
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

    @Transactional
    @Override
    public void addLike(Long filmId, Long userId) {
        update(INSERT_FILMS_LIKES, filmId, userId);
    }

    @Transactional
    @Override
    public void deleteLike(Long filmId, Long userId) {
        try {
            delete(DELETE_FILMS_LIKES, filmId, userId);
        } catch (FailedToDeleteException e) {
            log.trace("Failed to delete like by user [id={}] for film [id={}].", userId, filmId);
        }
    }

    private BatchPreparedStatementSetter getBatchPsSetterForFilmsGenres(Long filmId, List<Genre> genres) {
        return new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
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
            public void setValues(PreparedStatement ps, int i) throws SQLException {
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
