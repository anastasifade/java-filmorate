package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.dto.film.CreateFilmDto;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmDto;
import ru.yandex.practicum.filmorate.exceptions.DuplicateDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FilmControllerTests {

    private static final LocalDate VALID_RELEASE_DATE = LocalDate.of(2007, 11, 11);

    private FilmController controller;

    @BeforeEach
    void createController() {
        controller = new FilmController(new FilmService(new InMemoryFilmStorage(),
                new UserService(new InMemoryUserStorage())));
    }

    @Test
    void getFilms_ReturnsEmptyWhenNoFilms() {
        List<Film> emptyList = new ArrayList<>();
        Assertions.assertEquals(emptyList, controller.findAll());
    }

    @Test
    void getFilms_ReturnsListOfFilms() {
        CreateFilmDto film1 = new CreateFilmDto("title", VALID_RELEASE_DATE, 100, "description");
        CreateFilmDto film2 = new CreateFilmDto("other title", VALID_RELEASE_DATE, 100, "description");
        List<Film> expected = List.of(controller.create(film1), controller.create(film2));

        Assertions.assertEquals(expected, controller.findAll());
    }

    @Test
    void returnsFilmWithId_PostRequest() {
        CreateFilmDto filmDto = new CreateFilmDto("title", VALID_RELEASE_DATE, 100, "description");
        Film film = controller.create(filmDto);
        Assertions.assertNotNull(film.getId());
    }

    @Test
    void postRequest_failsWithDuplicateData() {
        CreateFilmDto filmDto = new CreateFilmDto("title", VALID_RELEASE_DATE, 100, "description");
        controller.create(filmDto);
        Assertions.assertThrows(DuplicateDataException.class, () -> controller.create(filmDto));
    }

    @Test
    void putRequest_updatesField() {
        CreateFilmDto filmDto = new CreateFilmDto("title", VALID_RELEASE_DATE, 100, "description");
        Film film = controller.create(filmDto);

        String expectedTitle = "new title";

        UpdateFilmDto updateFilmDto = new UpdateFilmDto();
        updateFilmDto.setId(film.getId());
        updateFilmDto.setName(expectedTitle);

        Film updatedFilm = controller.update(updateFilmDto);
        Assertions.assertEquals(expectedTitle, updatedFilm.getName());
    }

    @Test
    void putRequest_failsForUnusedId() {
        UpdateFilmDto updateFilmDto = new UpdateFilmDto();
        updateFilmDto.setId(10000L);
        updateFilmDto.setName("title");
        Assertions.assertThrows(NotFoundException.class, () -> controller.update(updateFilmDto));
    }
}
