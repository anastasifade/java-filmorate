package ru.yandex.practicum.filmorate.dal.genre;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.DbStorage;
import ru.yandex.practicum.filmorate.model.Genre;


@Repository
@Primary
public class GenreDbStorage extends DbStorage<Genre> {

    public GenreDbStorage(final JdbcTemplate template, final RowMapper<Genre> mapper) {
        super("genres", template, mapper);
    }

    @Override
    public Genre create(Genre obj) {
        throw new UnsupportedOperationException("POST /genre not supported by the API.");
    }

    @Override
    public Genre update(Genre obj) {
       throw new UnsupportedOperationException("PUT /genres not supported by the API.");
    }
}
