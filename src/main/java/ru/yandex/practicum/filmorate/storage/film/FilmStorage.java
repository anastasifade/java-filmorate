package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

public interface FilmStorage extends Storage<Film> {

    // TODO: add search parameter logic
    Collection<Film> findAllBy();

    Optional<Film> findBy(String title, LocalDate release, int duration);

}
