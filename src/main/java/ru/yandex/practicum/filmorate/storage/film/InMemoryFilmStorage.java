package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class InMemoryFilmStorage extends InMemoryStorage<Film> implements FilmStorage {

    @Override
    public List<Film> findAllBy() {
        return null;
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

}
