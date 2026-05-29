package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.enums.EventOperation;
import ru.yandex.practicum.filmorate.enums.EventType;

import java.time.Instant;


@Data
@EqualsAndHashCode(of = {"id"})
@Builder
@AllArgsConstructor
public class Event implements Entity {

    private Long id;
    private Long userId;
    private Long entityId;
    private Instant timestamp;
    private EventType eventType;
    private EventOperation operation;

}
