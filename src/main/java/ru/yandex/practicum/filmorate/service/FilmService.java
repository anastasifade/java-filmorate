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
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;

    public Collection<Film> findAll() {
        log.trace("GET /films request received by FilmService.");
        return filmStorage.findAll();
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
            log.info("POST /films request failed: film already exists, id {}.", existingFilm.get().getId());
            throw new DuplicateDataException(String.format("Film already exists, id [%d].", existingFilm.get().getId()));
        }

        Film film = Film.builder()
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .build();

        film = filmStorage.create(film);
        return film;
    }

    public Film update(UpdateFilmDto dto) {
        log.trace("PUT /films request received by FilmService.");
        Optional<Film> filmOptional = filmStorage.findById(dto.getId());

        if (filmOptional.isEmpty()) {
            log.info("Film with id={} not found.", dto.getId());
            throw new NotFoundException(String.format("Film with id [%d] not found.", dto.getId()));
        }

        Film film = filmOptional.get();

        Optional<Film> existingFilm = filmStorage.findBy(dto.getName() == null ? film.getName() : dto.getName(),
                dto.getReleaseDate() == null ? film.getReleaseDate() : dto.getReleaseDate(),
                dto.getDuration() == null ? film.getDuration() : dto.getDuration());

        if (existingFilm.isPresent() && existingFilm.get().getId() != dto.getId()) {
            log.debug("Update failed: attempted update to an already existing film. Existing film id: {}.",
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

}
