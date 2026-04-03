package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.time.LocalDate;
import java.util.Optional;

public interface FilmStorage extends Storage<Film> {

    Optional<Film> findBy(String title, LocalDate release, int duration);

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

}
