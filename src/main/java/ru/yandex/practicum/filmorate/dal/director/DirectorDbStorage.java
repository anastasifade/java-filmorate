package ru.yandex.practicum.filmorate.dal.director;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.DbStorage;
import ru.yandex.practicum.filmorate.exceptions.InternalServerException;
import ru.yandex.practicum.filmorate.model.Director;

@Transactional
@Repository
@Primary
public class DirectorDbStorage extends DbStorage<Director> {
    private static final String INSERT_DIRECTOR = """
            INSERT INTO directors (name)
            VALUES (?)
            """;

    private static final String UPDATE_DIRECTOR = """
            UPDATE directors
            SET name = ?
            WHERE id = ?
            """;

    public DirectorDbStorage(final JdbcTemplate jdbc, final RowMapper<Director> mapper) {
        super("directors", jdbc, mapper);
    }

    @Override
    public Director create(Director obj) {
        Long id = insert(INSERT_DIRECTOR, obj.getName());
        return findById(id).orElseThrow(() -> new InternalServerException("Failed to create director."));
    }

    @Override
    public Director update(Director obj) {
        update(UPDATE_DIRECTOR, obj.getName(), obj.getId());
        return findById(obj.getId()).orElseThrow(() ->
                new InternalServerException("Failed to update director information."));
    }
}
