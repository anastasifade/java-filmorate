package ru.yandex.practicum.filmorate.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum EventOperation {
    ADD(1),
    UPDATE(2),
    REMOVE(3);

    private static final Map<Integer, EventOperation> operations = new HashMap<>();

    static {
        for (EventOperation op : EventOperation.values())
            operations.put(op.id, op);
    }

    private final int id;

    EventOperation(int id) {
        this.id = id;
    }

    public static EventOperation getOperation(int id) {
        return operations.get(id);
    }
}
