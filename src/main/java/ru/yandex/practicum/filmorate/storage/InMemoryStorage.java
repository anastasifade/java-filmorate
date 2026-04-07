package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class InMemoryStorage<T extends Entity> implements Storage<T> {

    protected final Map<Long, T> storage = new HashMap<>();

    @Override
    public List<T> findAll() {
        return List.copyOf(storage.values());
    }

    @Override
    public Optional<T> findById(long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public T create(T obj) {
        obj.setId(getNextId());
        storage.put(obj.getId(), obj);
        return obj;
    }

    @Override
    public T update(T obj) {
        Long id = obj.getId();
        if (!storage.containsKey(id)) {
            throw new NotFoundException(String.format("Id %d not found. Entity not updated.", id));
        }
        storage.put(id, obj);
        return obj;
    }

    @Override
    public void delete(long id) {
        if (!storage.containsKey(id)) {
            throw new NotFoundException(String.format("Id %d not found. Entity not deleted.", id));
        }

        storage.remove(id);
    }

    Long getNextId() {
        Long nextId = storage.keySet().stream().mapToLong(id -> id).max().orElse(0L);
        return ++ nextId;
    }

}
