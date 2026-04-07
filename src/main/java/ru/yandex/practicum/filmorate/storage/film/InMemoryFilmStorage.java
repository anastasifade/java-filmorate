package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class InMemoryFilmStorage extends InMemoryStorage<Film> implements FilmStorage {

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

}
