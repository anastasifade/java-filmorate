package ru.yandex.practicum.filmorate.dto.event;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EventDto {
    Long eventId;
    Long userId;
    Long entityId;
    Long timestamp;
    String eventType;
    String operation;
}
