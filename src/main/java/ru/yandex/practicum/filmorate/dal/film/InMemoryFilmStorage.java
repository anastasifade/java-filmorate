package ru.yandex.practicum.filmorate.dal.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.InMemoryStorage;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage extends InMemoryStorage<Film> implements FilmStorage {

    private static final Comparator<Film> FILM_LIKE_COMPARATOR =
            (film1, film2) -> film2.getLikes().size() - film1.getLikes().size();

    //private final UndertowServletWebServerFactory undertowServletWebServerFactory;

    @Override
    public Collection<Film> findPopular(int count) {
        return findAll().stream().sorted(FILM_LIKE_COMPARATOR).limit(count).toList();
    }

    @Override
    public Optional<Film> findById(String name, LocalDate release, int duration) {
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
    public Collection<Film> getSharedMovies(Long userId, Long friendId) {
        return findAll().stream()
                .filter(film -> film.getLikes().contains(userId) && film.getLikes().contains(friendId))
                .collect(Collectors.toList());
    }

}
