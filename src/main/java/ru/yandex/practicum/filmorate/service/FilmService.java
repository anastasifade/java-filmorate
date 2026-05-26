package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.Id;
import ru.yandex.practicum.filmorate.dto.film.NewFilmDto;
import ru.yandex.practicum.filmorate.dto.film.ResponseFilmDto;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmDto;
import ru.yandex.practicum.filmorate.enums.SearchParam;
import ru.yandex.practicum.filmorate.enums.SortParam;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dal.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;
    private final MpaService mpaService;
    private final GenreService genreService;
    private final DirectorService directorService;

    public Collection<ResponseFilmDto> findAll() {
        log.trace("GET /films request received by FilmService.");
        return filmStorage.findAll()
                .stream()
                .map(FilmMapper::toDto)
                .toList();
    }

    public Collection<ResponseFilmDto> findPopular(int count, Long genreId, Integer year) {
        log.trace("GET /films/popular request received by FilmService.");

        return filmStorage.findPopular(count, genreId, year)
                .stream()
                .map(FilmMapper::toDto)
                .toList();
    }

    public Collection<ResponseFilmDto> findByDirectorSorted(Long directorId, SortParam sortBy) {
        log.trace("GET /films/director/{}?sortBy={} received by FilmService.", directorId, sortBy.name().toLowerCase());

        // validating director id
        directorService.findById(directorId);

        return filmStorage.findByDirectorSorted(directorId, sortBy)
                .stream()
                .map(FilmMapper::toDto)
                .toList();
    }

    public Collection<ResponseFilmDto> searchFilms(String query, String by) {
        log.trace("GET /films/search?query={}&by={}.", query, by);

        Set<SearchParam> searchParams = Arrays.stream(by.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .map(SearchParam::valueOf)
                .collect(Collectors.toSet());

        return filmStorage.searchFilmsByParams(query, searchParams)
                .stream()
                .map(FilmMapper::toDto)
                .toList();
    }

    public ResponseFilmDto findById(Long id) {
        log.trace("GET /films/{} request received by FilmService.", id);
        Optional<Film> filmOptional = filmStorage.findById(id);
        if (filmOptional.isEmpty()) {
            throwNotFound(id);
        }
        return FilmMapper.toDto(filmOptional.get());
    }

    public ResponseFilmDto create(NewFilmDto dto) {
        log.trace("POST /films request received by FilmService.");

        validateMpa(dto.getMpa().getId());
        if (dto.getGenres() != null) {
            validateGenres(dto.getGenres());
        }

        if (dto.getDirectors() != null) {
            validateDirectors(dto.getDirectors());
        }

        Film film = FilmMapper.toFilm(dto);
        film = filmStorage.create(film);
        log.debug("Created film: {}.", film);

        return FilmMapper.toDto(film);
    }

    public ResponseFilmDto update(UpdateFilmDto dto) {
        log.trace("PUT /films request received by FilmService.");

        Optional<Film> filmOptional = filmStorage.findById(dto.getId());
        if (filmOptional.isEmpty()) {
            throwNotFound(dto.getId());
        }

        if (dto.getMpa() != null) {
            validateMpa(dto.getMpa().getId());
        }
        if (dto.getGenres() != null && !(dto.getGenres().isEmpty())) {
            validateGenres(dto.getGenres());
        }
        if (dto.getDirectors() != null && !(dto.getDirectors().isEmpty())) {
            validateDirectors(dto.getDirectors());
        }

        Film film = FilmMapper.toFilm(dto, filmOptional.get());
        film = filmStorage.update(film);
        log.debug("Updated film: {}.", film);

        return FilmMapper.toDto(film);
    }

    public void addLike(Long filmId, Long userId) {
        log.trace("PUT /films/{}/likes/{} receives by FilmService.", filmId, userId);
        Optional<Film> filmOptional = filmStorage.findById(filmId);
        if (filmOptional.isEmpty()) {
            throwNotFound(filmId);
        }
        // throws NotFoundException in userService if user is not found
        userService.findById(userId);

        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        log.trace("DELETE /films/{}/likes/{} receives by FilmService.", filmId, userId);
        Optional<Film> filmOptional = filmStorage.findById(filmId);
        if (filmOptional.isEmpty()) {
            throwNotFound(filmId);
        }
        // throws NotFoundException in userService if user is not found
        userService.findById(userId);

        filmStorage.deleteLike(filmId, userId);
    }

    public Collection<ResponseFilmDto> recommend(Long userId) {
        log.trace("GET /users/{}/recommendations received by FilmService.", userId);
        userService.findById(userId); // validating userId
        return filmStorage.recommend(userId)
                .stream()
                .map(FilmMapper::toDto)
                .toList();
    }

    private void throwNotFound(long id) {
        log.warn("Film with id={} not found.", id);
        throw new NotFoundException(String.format("Film with id [%d] not found.", id));
    }

    private void validateMpa(Long id) {
        mpaService.findById(id);
    }

    private void validateGenres(Set<Id> genres) {
        genres.stream()
                .map(id -> id.getId())
                .forEach(genreService::findById);
    }

    private void validateDirectors(Set<Id> directors) {
        directors.stream()
                .map(id -> id.getId())
                .forEach(directorService::findById);
    }

    public void delete(Long id) {
        log.trace("DELETE /films/{} request received by FilmService.", id);

        if (filmStorage.findById(id).isEmpty()) {
            throwNotFound(id);
        }

        filmStorage.delete(id);
    }
}
