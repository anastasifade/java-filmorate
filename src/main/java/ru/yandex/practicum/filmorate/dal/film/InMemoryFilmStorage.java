package ru.yandex.practicum.filmorate.dal.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.enums.SearchParam;
import ru.yandex.practicum.filmorate.enums.SortParam;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dal.InMemoryStorage;

import java.time.LocalDate;
import java.util.*;

@Component
public class InMemoryFilmStorage extends InMemoryStorage<Film> implements FilmStorage {

    private static final Comparator<Film> FILM_LIKE_COMPARATOR =
            (film1, film2) -> film2.getLikes().size() - film1.getLikes().size();

    @Override
    public Collection<Film> findPopular(int count) {
        return findAll().stream().sorted(FILM_LIKE_COMPARATOR).limit(count).toList();
    }

    @Override
    public Collection<Film> findByDirectorSorted(Long directorId, SortParam sortBy) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Film> findBy(String name, LocalDate release, int duration) {
        return storage.values()
                .stream()
                .filter(film -> film.getName().equalsIgnoreCase(name) &&
                        film.getReleaseDate().equals(release) &&
                        film.getDuration() == duration)
                .findFirst();
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
}
