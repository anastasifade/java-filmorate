package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.CreateFilmDto;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmDto;
import ru.yandex.practicum.filmorate.exceptions.DuplicateDataException;
import ru.yandex.practicum.filmorate.exceptions.MalformedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Handling /GET films.");
        return List.copyOf(films.values());
    }

    @PostMapping
    public Film create(@Valid @RequestBody CreateFilmDto filmDto) {
        log.info("Handling POST /films request.");
        log.debug("POST request to create object: {}.", filmDto);

        int duration = filmDto.getDuration();
        LocalDate releaseDate = filmDto.getReleaseDate();
        String title = filmDto.getName().trim();
        String description = filmDto.getDescription();
        if (filmDto.getDescription() != null) {
            description = description.trim();
        }

        validateReleaseDate(releaseDate);
        Optional<Long> existingId = findExistingId(title, releaseDate, duration);
        if (existingId.isPresent()) {
            log.info("POST /films request failed: film already exists, id {}.", existingId.get());
            throw new DuplicateDataException(String.format("Film already exists, id [%s].", existingId.get()));
        }

        Film film = Film.builder()
                .id(getNextId())
                .name(title)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .build();

        films.put(film.getId(), film);
        log.info("Created object: {}.", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody UpdateFilmDto filmDto) {
        log.info("Handling PUT /films request.");
        log.debug("PUT /films request for: {}.", filmDto);

        if (!films.containsKey(filmDto.getId())) {
            log.info("Film with id={} not found.", filmDto.getId());
            throw new NotFoundException(String.format("Film with id [%s] not found.", filmDto.getId()));
        }

        Film film = films.get(filmDto.getId());

        if (filmDto.getName() != null && !filmDto.getName().isBlank()) {
            film.setName(filmDto.getName().trim());
            log.debug("Updated field [title] to: {}.", film.getName());
        }

        if (filmDto.getReleaseDate() != null) {
            validateReleaseDate(filmDto.getReleaseDate());
            film.setReleaseDate(filmDto.getReleaseDate());
            log.debug("Updated field [release date] to: {}.", film.getReleaseDate());
        }

        if (filmDto.getDuration() != null) {
            film.setDuration(filmDto.getDuration());
            log.debug("Updated field [duration] to: {}.", film.getDuration());
        }

        if (filmDto.getDescription() != null) {
            String description = filmDto.getDescription().trim();
            film.setDescription(description);
            log.debug("Updated field [description] to: {}.", film.getDescription());
        }

        return film;
    }

    private Long getNextId() {
        Long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }


    /**
    *  VALIDATION
     **/

    private void validateReleaseDate(LocalDate release) {
        if (release.isBefore(MIN_RELEASE_DATE)) {
            log.info("Request failed: invalid release date.");
            throw new MalformedDataException(String.format("Release date cannot be before %s.",
                    MIN_RELEASE_DATE));
        }
    }

    private Optional<Long> findExistingId(String title, LocalDate releaseDate, int duration) {
        return films.values()
                    .stream()
                    .filter(film -> film.getName().equalsIgnoreCase(title) &&
                            film.getReleaseDate().equals(releaseDate) &&
                            film.getDuration() == duration)
                    .map(Film::getId)
                    .findFirst();
    }

}
