package ru.yandex.practicum.filmorate.enums;

import java.util.HashMap;
import java.util.Map;

public enum EventType {
    LIKE(1),
    REVIEW(2),
    FRIEND(3);

    private static final Map<Integer, EventType> types = new HashMap<>();

    static {
        for (EventType type : EventType.values())
            types.put(type.id, type);
    }

    private final int id;

    private EventType(int id) {
        this.id = id;
    }

    public static EventType getType(int id) {
        return types.get(id);
    }

    public int getId() {
        return this.id;
    }
}
