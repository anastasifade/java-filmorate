package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.enums.EventOperation;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EventRowMapper implements RowMapper<Event> {

    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .id(rs.getLong("id"))
                .userId(rs.getLong("user_id"))
                .entityId(rs.getLong("entity_id"))
                .timestamp(rs.getLong("timestamp"))
                .eventType(EventType.getType((int) rs.getLong("event_type")))
                .operation(EventOperation.getOperation((int) rs.getLong("operation")))
                .build();
    }
}
