package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.CreateFilmDto;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmDto;
import ru.yandex.practicum.filmorate.exceptions.DuplicateDataException;
import ru.yandex.practicum.filmorate.exceptions.MalformedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {

    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1985, 12, 28);

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return List.copyOf(films.values());
    }

    @PostMapping
    public Film create(@Valid @RequestBody CreateFilmDto filmDto) {
        validateReleaseDate(filmDto.getReleaseDate());
        validateDuration(filmDto.getDuration());
        validateDescription(filmDto.getDescription());

        Optional<Long> existingId = findExistingId(filmDto.getTitle(), filmDto.getReleaseDate(), filmDto.getDuration());
        if (existingId.isPresent()) {
            throw new DuplicateDataException(String.format("Film already exists, id [%s].", existingId.get()));
        }

        Film film = toFilm(filmDto);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody UpdateFilmDto filmDto) {
        if (filmDto.getId() < 0) {
            throw new MalformedDataException("Invalid id - id must be greater than 0.");
        }

        if (!films.containsKey(filmDto.getId())) {
            throw new NotFoundException(String.format("Film with id [%s] not found.", filmDto.getId()));
        }

        Film film = films.get(filmDto.getId());

        if (filmDto.getTitle() != null && !filmDto.getTitle().isBlank()) {
            film.setTitle(filmDto.getTitle());
        }

        if (filmDto.getReleaseDate() != null) {
            validateReleaseDate(filmDto.getReleaseDate());
            film.setReleaseDate(filmDto.getReleaseDate());
        }

        if (filmDto.getDuration() != null) {
            validateDuration(filmDto.getDuration());
            film.setDuration(filmDto.getDuration());
        }

        if (filmDto.getDescription() != null) {
            validateDescription(filmDto.getDescription());
            film.setDescription(filmDto.getDescription());
        }

        return film;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private Film toFilm(CreateFilmDto dto) {
        return Film.builder()
                .id(getNextId())
                .title(dto.getTitle())
                .releaseDate(dto.getReleaseDate())
                .duration(dto.getDuration())
                .description(dto.getDescription())
                .build();
    }


    /**
    *  VALIDATION
     **/

    private void validateReleaseDate(LocalDate release) {
        if (release.isBefore(MIN_RELEASE_DATE)) {
            throw new MalformedDataException(String.format("Release date cannot be before %s.",
                    MIN_RELEASE_DATE.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
        }
    }

    private void validateDuration(Duration duration) {
        if(!duration.isPositive()) {
            throw new MalformedDataException("Film duration must be greater than 0.");
        }
    }

    private void validateDescription(String description) {
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new MalformedDataException("Description cannot be longer than 200 characters.");
        }
    }

    private Optional<Long> findExistingId(String title, LocalDate releaseDate, Duration duration) {
        return films.values()
                    .stream()
                    .filter(film -> film.getTitle().equals(title) &&
                            film.getReleaseDate().equals(releaseDate) &&
                            film.getDuration().equals(duration))
                    .map(Film::getId)
                    .findFirst();
    }

}
