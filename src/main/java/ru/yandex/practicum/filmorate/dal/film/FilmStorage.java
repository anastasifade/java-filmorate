package ru.yandex.practicum.filmorate.dal.film;

import ru.yandex.practicum.filmorate.enums.SearchParam;
import ru.yandex.practicum.filmorate.enums.SortParam;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dal.Storage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage extends Storage<Film> {

    Collection<Film> findPopular(int count);

    Collection<Film> findByDirectorSorted(Long directorId, SortParam sortBy);

    Optional<Film> findBy(String name, LocalDate releaseDate, int duration);

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    Collection<Film> searchFilmsByParams(String query, Set<SearchParam> searchParams);

    Collection<Film> recommend(Long userId);
}
