package ru.yandex.practicum.filmorate.dal.film;

import ru.yandex.practicum.filmorate.enums.SearchParam;
import ru.yandex.practicum.filmorate.enums.SortParam;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dal.Storage;

import java.util.Collection;
import java.util.Set;

public interface FilmStorage extends Storage<Film> {

    Collection<Film> findPopular(int count, Long genreId, Integer year);

    Collection<Film> findCommonFilms(Long userId, Long friendId);

    Collection<Film> findByDirectorSorted(Long directorId, SortParam sortBy);

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    Collection<Film> searchFilmsByParams(String query, Set<SearchParam> searchParams);

    Collection<Film> recommend(Long userId);
}
