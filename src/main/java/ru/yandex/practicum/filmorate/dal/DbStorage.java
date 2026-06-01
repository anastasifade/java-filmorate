package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exceptions.InternalServerException;
import ru.yandex.practicum.filmorate.model.Entity;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Transactional
@Slf4j
public abstract class DbStorage<T extends Entity> implements Storage<T> {
    protected final String table;
    protected final JdbcTemplate jdbc;
    protected final RowMapper<T> mapper;

    public DbStorage(String table, final JdbcTemplate jdbc, final RowMapper<T> mapper) {
        this.table = table;
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<T> findAll() {
        return jdbc.query(getFindAllQuery(), mapper);
    }

    @Transactional(readOnly = true)
    protected Collection<T> findMany(String query, Object... params) {
        return jdbc.query(query, mapper, params);
    }

    @Transactional(readOnly = true)
    protected Collection<T> findMany(String query, ResultSetExtractor<List<T>> extractor, Object... params) {
        return jdbc.query(query, extractor, params);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<T> findById(long id) {
        try {
            T result = jdbc.queryForObject(getFindByIdQuery(), mapper, id);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Transactional(readOnly = true)
    public Optional<T> findOne(String query, Object... params) {
        try {
            T result = jdbc.queryForObject(query, mapper, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public abstract T create(T obj);

    protected Long insert(String query, Object... params) {
        log.trace("Creating object. Query: {}. Params: {}.", query, params);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);

        if (id != null) {
            log.trace("Object created successfully. Id: {}.", id);
            return id;
        } else {
            log.error("Failed to insert data.");
            throw new InternalServerException("Failed to insert data.");
        }
    }

    @Override
    public abstract T update(T obj);

    protected void update(String query, Object... params) {
        int rowsUpdated = jdbc.update(query, params);
        if (rowsUpdated == 0) throw new InternalServerException("Failed to update data.");
    }

    protected int[] batchUpdate(String query, BatchPreparedStatementSetter setter) {
        return jdbc.batchUpdate(query, setter);
    }

    @Override
    public void delete(long id) {
        int rowsDeleted = jdbc.update(getDeleteQuery(), id);
        if (rowsDeleted == 0) log.debug("No objects deleted.");
    }

    protected void delete(String query, Object... params) {
        int rowsDeleted = jdbc.update(query, params);
        if (rowsDeleted == 0) log.debug("No objects deleted.");
    }

    protected String getFindAllQuery() {
        return String.format("SELECT * FROM %s ORDER BY id;", table);
    }

    protected String getFindByIdQuery() {
        return String.format("SELECT * FROM %s WHERE id = ?;", table);
    }

    protected String getDeleteQuery() {
        return String.format("DELETE FROM %s WHERE id = ?;", table);
    }
}
