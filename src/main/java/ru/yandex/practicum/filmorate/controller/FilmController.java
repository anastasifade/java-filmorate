package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.CreateFilmDto;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Handling /GET films.");
        return filmService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody CreateFilmDto dto) {
        log.info("Handling POST /films request.");
        log.debug("POST request to create object: {}.", dto);
        Film film = filmService.create(dto);
        log.info("Created object: {}.", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody UpdateFilmDto dto) {
        log.info("Handling PUT /films request.");
        log.debug("PUT /films request for: {}.", dto);

        Film film = filmService.update(dto);
        log.debug("Updated object: {}.", film);
        return film;
    }
}
