package ru.yandex.practicum.filmorate.dal.event;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.DbStorage;
import ru.yandex.practicum.filmorate.exceptions.InternalServerException;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.Timestamp;
import java.util.Collection;

@Transactional
@Repository
@Primary
public class EventDbStorage extends DbStorage<Event> {

    private static final String INSERT_EVENT = """
            INSERT INTO events (user_id, entity_id, timestamp, event_type, operation)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String GET_FEED = """
            SELECT id, user_id, entity_id, timestamp, event_type, operation
            FROM events
            WHERE user_id = ?
            """;

    public EventDbStorage(final JdbcTemplate jdbc, final RowMapper<Event> mapper) {
        super("events", jdbc, mapper);
    }

    public Collection<Event> getFeed(Long userId) {
        return findMany(GET_FEED, userId);
    }

    @Override
    public Event create(Event obj) {
        Long id = insert(INSERT_EVENT,
                obj.getUserId(),
                obj.getEntityId(),
                Timestamp.from(obj.getTimestamp()),
                Long.valueOf(obj.getEventType().getId()),
                Long.valueOf(obj.getOperation().getId()));

        return findById(id).orElseThrow(() -> new InternalServerException("Failed to create an event."));
    }

    @Override
    public Event update(Event obj) {
        throw new UnsupportedOperationException("Cannot update an event.");
    }
}
