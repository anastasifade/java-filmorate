package ru.yandex.practicum.filmorate.mappers;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.event.EventDto;
import ru.yandex.practicum.filmorate.enums.EventOperation;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.model.Event;

import java.time.Instant;

@UtilityClass
public final class EventMapper {
    public static EventDto toDto(Event event) {
        return EventDto.builder()
                .eventId(event.getId())
                .userId(event.getUserId())
                .entityId(event.getEntityId())
                .eventType(event.getEventType().name())
                .operation(event.getOperation().name())
                .timestamp(event.getTimestamp())
                .build();
    }

    public static Event newEvent(Long userId, Long entityId, EventType type, EventOperation operation) {
        return Event.builder()
                .userId(userId)
                .entityId(entityId)
                .eventType(type)
                .operation(operation)
                .timestamp(Instant.now().toEpochMilli())
                .build();
    }
}
