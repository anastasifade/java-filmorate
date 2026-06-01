package ru.yandex.practicum.filmorate.dto.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventDto {

    private Long eventId;
    private Long userId;
    private Long entityId;
    private Long timestamp;
    private String eventType;
    private String operation;

}
