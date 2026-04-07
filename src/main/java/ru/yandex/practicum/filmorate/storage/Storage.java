package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Entity;

import java.util.Collection;
import java.util.Optional;

public interface Storage<T extends Entity> {

    Collection<T> findAll();

    Optional<T> findById(long id);

    T create(T obj);

    T update(T obj);

    void delete(long id);

}
