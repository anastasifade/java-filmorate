package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.dto.film.CreateFilmDto;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmDto;
import ru.yandex.practicum.filmorate.exceptions.DuplicateDataException;
import ru.yandex.practicum.filmorate.exceptions.MalformedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FilmControllerTests {

    private static final LocalDate VALID_RELEASE_DATE = LocalDate.of(2007, 11, 11);
    private static final LocalDate INVALID_RELEASE_DATE = LocalDate.of(1800, 01, 01);

    private FilmController controller;

    @BeforeEach
    void createController() {
        controller = new FilmController();
    }

    @Test
    void getFilms_ReturnsEmptyWhenNoFilms() {
        List<Film> emptyList = new ArrayList<>();
        Assertions.assertEquals(emptyList, controller.findAll());
    }

    @Test
    void getFilms_ReturnsListOfFilms() {
        CreateFilmDto film1 = CreateFilmDto.builder()
                .title("title")
                .releaseDate(VALID_RELEASE_DATE)
                .duration(100)
                .build();
        CreateFilmDto film2 = CreateFilmDto.builder()
                .title("other title")
                .releaseDate(VALID_RELEASE_DATE)
                .duration(50)
                .build();
        List<Film> expected = List.of(controller.create(film1), controller.create(film2));

        Assertions.assertEquals(expected, controller.findAll());
    }

    @Test
    void returnsFilmWithId_PostRequest() {
        CreateFilmDto filmDto = CreateFilmDto.builder()
                .title("title")
                .releaseDate(VALID_RELEASE_DATE)
                .duration(100)
                .build();
        Film film = controller.create(filmDto);
        Assertions.assertNotNull(film.getId());
    }

    @Test
    void postRequest_failsWithDuplicateData() {
        CreateFilmDto filmDto = CreateFilmDto.builder()
                .title("title")
                .releaseDate(VALID_RELEASE_DATE)
                .duration(100)
                .build();
        controller.create(filmDto);
        Assertions.assertThrows(DuplicateDataException.class, () -> controller.create(filmDto));
    }

    @Test
    void postRequest_failsWithInvalidReleaseDate() {
        CreateFilmDto filmDto = CreateFilmDto.builder()
                .title("title")
                .releaseDate(INVALID_RELEASE_DATE)
                .duration(100)
                .build();
        Assertions.assertThrows(MalformedDataException.class, () -> controller.create(filmDto));
    }

    @Test
    void putRequest_updatesField() {
        CreateFilmDto filmDto = CreateFilmDto.builder()
                .title("title")
                .releaseDate(VALID_RELEASE_DATE)
                .duration(100)
                .build();
        Film film = controller.create(filmDto);
        String expectedTitle = "new title";
        UpdateFilmDto updateFilmDto = UpdateFilmDto.builder().id(film.getId()).title(expectedTitle).build();
        Film updatedFilm = controller.update(updateFilmDto);

        Assertions.assertEquals(expectedTitle, updatedFilm.getTitle());
    }

    @Test
    void putRequest_failsForUnusedId() {
        UpdateFilmDto updateFilmDto = UpdateFilmDto.builder().id(700L).title("title").build();
        Assertions.assertThrows(NotFoundException.class, () -> controller.update(updateFilmDto));
    }
}
