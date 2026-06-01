package ru.yandex.practicum.filmorate.dal.mpa;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.DbStorage;
import ru.yandex.practicum.filmorate.model.MPA;

@Repository
@Primary
public class MpaDbStorage extends DbStorage<MPA> {
    public MpaDbStorage(final JdbcTemplate template, final RowMapper<MPA> mapper) {
        super("mpa", template, mapper);
    }

    @Override
    public MPA create(MPA obj) {
        throw new UnsupportedOperationException("POST /mpa not supported by the API.");
    }

    @Override
    public MPA update(MPA obj) {
        throw new UnsupportedOperationException("PUT /mpa not supported by the API.");
    }
}
