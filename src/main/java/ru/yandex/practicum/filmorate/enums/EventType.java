package ru.yandex.practicum.filmorate.enums;

import java.util.HashMap;
import java.util.Map;

public enum EventType {
    LIKE(1),
    REVIEW(2),
    FRIEND(3);

    private final int id;

    private static final Map<Integer, EventType> types = new HashMap<>();

    static {
        for (EventType type : EventType.values()) {
            types.put(type.id, type);
        }
    }

    private EventType(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static EventType getType(int id) {
        return types.get(id);
    }
}
