package ru.yandex.practicum.filmorate.dal.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.InMemoryStorage;
import ru.yandex.practicum.filmorate.enums.SearchParam;
import ru.yandex.practicum.filmorate.enums.SortParam;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

@Component
public final class InMemoryFilmStorage extends InMemoryStorage<Film> implements FilmStorage {
    private static final Comparator<Film> FILM_LIKE_COMPARATOR =
            (film1, film2) -> film2.getLikes().size() - film1.getLikes().size();

    @Override
    public Collection<Film> findPopular(int count, Long genreId, Integer year) {
        return findAll().stream().sorted(FILM_LIKE_COMPARATOR).limit(count).toList();
    }

    @Override
    public Collection<Film> findCommonFilms(Long userId, Long friendId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Film> findByDirectorSorted(Long directorId, SortParam sortBy) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        storage.get(filmId).getLikes().add(userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        storage.get(filmId).getLikes().remove(userId);
    }

    @Override
    public Collection<Film> searchFilmsByParams(String query, Set<SearchParam> searchParams) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Film> recommend(Long userId) {
        throw new UnsupportedOperationException();
    }
}
