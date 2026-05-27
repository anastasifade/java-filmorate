package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.NewFilmDto;
import ru.yandex.practicum.filmorate.dto.film.ResponseFilmDto;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmDto;
import ru.yandex.practicum.filmorate.enums.SortParam;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public Collection<ResponseFilmDto> findAll() {
        log.info("Handling GET /films.");
        return filmService.findAll();
    }

    @GetMapping("/popular")
    public Collection<ResponseFilmDto> findPopular(@RequestParam(defaultValue = "10") int count,
                                                   @RequestParam(required = false) Long genreId,
                                                   @RequestParam(required = false) Integer year) {
        log.info("Handling GET /films/popular");
        return filmService.findPopular(count, genreId, year);
    }

    @GetMapping("/director/{id}")
    public Collection<ResponseFilmDto> findByDirectorSorted(@PathVariable Long id,
                                                            @RequestParam(defaultValue = "likes") String sortBy) {
        log.info("Handling GET /films/director/{}?sortBy={}.", id, sortBy);

        try {
            SortParam sortParam = SortParam.valueOf(sortBy.toUpperCase());
            return filmService.findByDirectorSorted(id, sortParam);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException("Unsupported query parameter.");
        }
    }

    @GetMapping("/{id}")
    public ResponseFilmDto findById(@PathVariable Long id) {
        log.info("Handling GET /films/{}.", id);
        return filmService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseFilmDto create(@Valid @RequestBody NewFilmDto dto) {
        log.info("Handling POST /films request.");
        log.debug("POST request to create object: {}.", dto);
        ResponseFilmDto film = filmService.create(dto);
        log.info("Created object: {}.", film);
        return film;
    }

    @PutMapping
    public ResponseFilmDto update(@RequestBody UpdateFilmDto dto) {
        log.info("Handling PUT /films request.");
        log.debug("PUT /films request for: {}.", dto);

        ResponseFilmDto film = filmService.update(dto);
        log.debug("Updated object: {}.", film);
        return film;
    }

    @GetMapping("/search")
    public Collection<ResponseFilmDto> searchFilms(@RequestParam String query, @RequestParam String by) {
        log.info("Handling GET /films/search?query={}&by={}.", query, by);
        return filmService.searchFilms(query, by);
    }

    @DeleteMapping("/{filmId}")
    public void delete(@PathVariable Long filmId) {
        log.info("Handling DELETE /films/{}.", filmId);
        filmService.delete(filmId);
    }

    @GetMapping("/common")
    public Collection<ResponseFilmDto> findCommonFilms(@RequestParam Long userId, @RequestParam Long friendId) {
        log.info("Handling GET GET /films/common?userId={}&friendId={}.", userId, friendId);
        return filmService.findCommonFilms(userId, friendId);
    }
}
