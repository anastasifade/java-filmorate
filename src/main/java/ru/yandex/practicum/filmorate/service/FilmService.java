package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.CreateFilmDto;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmDto;
import ru.yandex.practicum.filmorate.exceptions.DuplicateDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private static final Comparator<Film> FILM_LIKE_COMPARATOR =
            (film1, film2) -> film2.getLikes().size() - film1.getLikes().size();

    private final FilmStorage filmStorage;
    private final UserService userService;

    public Collection<Film> findAll() {
        log.trace("GET /films request received by FilmService.");
        return filmStorage.findAll();
    }

    public Collection<Film> findPopular(int count) {
        log.trace("GET /films/popular?count={} request received by FilmService.", count);
        return filmStorage.findAll().stream().sorted(FILM_LIKE_COMPARATOR).limit(count).toList();
    }

    public Film findById(Long id) {
        log.trace("GET /films/{} request received by FilmService.", id);
        Optional<Film> filmOptional = filmStorage.findById(id);

        if (filmOptional.isEmpty()) {
            throwNotFound(id);
        }

        return filmOptional.get();
    }

    public Film create(CreateFilmDto dto) {
        log.trace("POST /films request received by FilmService.");
        int duration = dto.getDuration();
        LocalDate releaseDate = dto.getReleaseDate();
        String name = dto.getName().trim();
        String description = dto.getDescription();
        if (dto.getDescription() != null) {
            description = description.trim();
        }

        Optional<Film> existingFilm = filmStorage.findBy(name, releaseDate, duration);
        if (existingFilm.isPresent()) {
            log.warn("POST /films request failed: film already exists, id {}.", existingFilm.get().getId());
            throw new DuplicateDataException(String.format("Film already exists, id [%d].", existingFilm.get().getId()));
        }

        Film film = Film.builder()
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .likes(new HashSet<>())
                .build();

        film = filmStorage.create(film);
        return film;
    }

    public Film update(UpdateFilmDto dto) {
        log.trace("PUT /films request received by FilmService.");
        Optional<Film> filmOptional = filmStorage.findById(dto.getId());

        if (filmOptional.isEmpty()) {
            throwNotFound(dto.getId());
        }

        Film film = filmOptional.get();

        Optional<Film> existingFilm = filmStorage.findBy(dto.getName() == null ? film.getName() : dto.getName(),
                dto.getReleaseDate() == null ? film.getReleaseDate() : dto.getReleaseDate(),
                dto.getDuration() == null ? film.getDuration() : dto.getDuration());

        if (existingFilm.isPresent() && !existingFilm.get().getId().equals(dto.getId())) {
            log.warn("Update failed: attempted update to an already existing film. Existing film id: {}.",
                    existingFilm.get().getId());
            log.trace("Attempted to update film: {}.{}Attempted to update fields to:{}.{}Conflicting film: {}.",
                    film, System.lineSeparator(),
                    dto, System.lineSeparator(),
                    existingFilm.get());
            throw new DuplicateDataException(String.format("Attempting to update to an already existing film, " +
                    "existing film id: [%d]. Update failed.", existingFilm.get().getId()));
        }

        if (dto.getName() != null) {
            film.setName(dto.getName().trim());
            log.debug("Updated field [title] to: {}.", film.getName());
        }

        if (dto.getReleaseDate() != null) {
            film.setReleaseDate(dto.getReleaseDate());
            log.debug("Updated field [release date] to: {}.", film.getReleaseDate());
        }

        if (dto.getDuration() != null) {
            film.setDuration(dto.getDuration());
            log.debug("Updated field [duration] to: {}.", film.getDuration());
        }

        if (dto.getDescription() != null) {
            String description = dto.getDescription().trim();
            film.setDescription(description);
            log.debug("Updated field [description] to: {}.", film.getDescription());
        }

        film = filmStorage.update(film);
        return film;
    }

    public void addLike(Long filmId, Long userId) {
        Optional<Film> filmOptional = filmStorage.findById(filmId);
        if (filmOptional.isEmpty()) {
            throwNotFound(filmId);
        }

        // throws NotFoundException in userService if user is not found
        userService.findById(userId);

        Film film = filmOptional.get();
        if (film.getLikes().contains(userId)) {
            log.debug("Attempt to add like to film id={} from user id={}: the user has already liked the film in the past." +
                    "No changes done.", filmId, userId);
            return;
        }

        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        Optional<Film> filmOptional = filmStorage.findById(filmId);
        if (filmOptional.isEmpty()) {
            throwNotFound(filmId);
        }

        // throws NotFoundException in userService if user is not found
        userService.findById(userId);

        Film film = filmOptional.get();
        if (!film.getLikes().contains(userId)) {
            log.debug("Attempt to delete like from film id={} by user id={}: user has not liked the film in the past." +
                    "Nothing to delete.", filmId, userId);
            return;
        }

        filmStorage.deleteLike(filmId, userId);
    }

    private void throwNotFound(long id) {
        log.warn("Film with id={} not found.", id);
        throw new NotFoundException(String.format("Film with id [%d] not found.", id));
    }

}
