package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.enums.EventOperation;
import ru.yandex.practicum.filmorate.enums.EventType;

@Data
@EqualsAndHashCode(of = {"id"})
@Builder
@AllArgsConstructor
public final class Event implements Entity {

    private Long id;
    private Long userId;
    private Long entityId;
    private Long timestamp;
    private EventType eventType;
    private EventOperation operation;

}
