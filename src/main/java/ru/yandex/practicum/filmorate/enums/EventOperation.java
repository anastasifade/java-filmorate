package ru.yandex.practicum.filmorate.enums;

import java.util.HashMap;
import java.util.Map;

public enum EventOperation {

    ADD(1),
    UPDATE(2),
    REMOVE(3);

    private final int id;

    private static final Map<Integer, EventOperation> operations = new HashMap<>();

    static {
        for (EventOperation op : EventOperation.values()) {
            operations.put(op.id, op);
        }
    }

    private EventOperation(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static EventOperation getOperation(int id) {
        return operations.get(id);
    }

}
