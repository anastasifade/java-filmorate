package ru.yandex.practicum.filmorate.dal.film;

import ru.yandex.practicum.filmorate.dal.Storage;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

public interface FilmStorage extends Storage<Film> {

    Collection<Film> findPopular(int count);

    Optional<Film> findById(String name, LocalDate releaseDate, int duration);

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    Collection<Film> getSharedMovies(Long user1Id, Long user2Id);
}
