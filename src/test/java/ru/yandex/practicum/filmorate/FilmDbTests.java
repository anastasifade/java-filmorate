package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.film.DbFilmStorage;
import ru.yandex.practicum.filmorate.dal.mappers.FilmResultSetMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({DbFilmStorage.class, FilmResultSetMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbTests {

    private final DbFilmStorage filmStorage;

    @Test
    public void findAllFilmsTest() {
        List<Film> films = filmStorage.findAll().stream().toList();
        Assertions.assertFalse(films.isEmpty());
        assertThat(films.getFirst())
                .hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    public void findFilmByIdTest() {
        Optional<Film> filmOptional = filmStorage.findById(1);

        assertThat(filmOptional)
                .isPresent()
                .satisfies(film ->
                        assertThat(film.get()).hasFieldOrPropertyWithValue("id", 1L));
    }

    // Testing: add like, delete like, find popular
    @Test
    public void findPopularTest() {
        Film newFilm = Film.builder()
                .name("newFilm")
                .description("new film description")
                .releaseDate(LocalDate.of(2015, 1, 1))
                .duration(150)
                .mpa(new MPA(1L, null))
                .build();
        newFilm = filmStorage.create(newFilm);

        filmStorage.addLike(newFilm.getId(), 1L);

        List<Film> films = filmStorage.findPopular(1, null, null).stream().toList();
        Assertions.assertFalse(films.isEmpty());
        assertThat(films.getFirst())
                .hasFieldOrPropertyWithValue("id", newFilm.getId());

        filmStorage.deleteLike(newFilm.getId(), 1L);

        films = filmStorage.findPopular(1, null, null).stream().toList();
        Assertions.assertFalse(films.isEmpty());
        assertThat(films.getFirst())
                .hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    public void createFilmTest() {
        String name = "create_film_test";
        String description = "123";
        LocalDate releaseDate = LocalDate.of(2020, 12, 12);
        int duration = 123;
        MPA mpa = new MPA(2L, null);
        Set<Genre> genres = Set.of(new Genre(1L, null));

        Film newFilm = Film.builder()
                .name(name)
                .duration(duration)
                .description(description)
                .releaseDate(releaseDate)
                .mpa(mpa)
                .genres(genres)
                .build();

        newFilm = filmStorage.create(newFilm);

        assertThat(newFilm)
                .hasFieldOrProperty("id")
                .satisfies(film -> assertThat(film.getId()).isNotNull())
                .hasFieldOrPropertyWithValue("name", name)
                .hasFieldOrPropertyWithValue("description", description)
                .hasFieldOrPropertyWithValue("releaseDate", releaseDate)
                .hasFieldOrPropertyWithValue("duration", duration)
                .hasFieldOrProperty("mpa")
                .satisfies(film -> assertThat(film.getMpa())
                        .hasFieldOrPropertyWithValue("id", 2L)
                        .hasFieldOrPropertyWithValue("name", "PG"))
                .hasFieldOrProperty("genres")
                .satisfies(film -> Assertions.assertFalse(film.getGenres().isEmpty()))
                .satisfies(film -> assertThat(film.getGenres().stream().findFirst())
                        .satisfies(genre -> assertThat(genre)
                                .isPresent()
                                .satisfies(g -> assertThat(g.get())
                                        .hasFieldOrPropertyWithValue("id", 1L)
                                        .hasFieldOrPropertyWithValue("name", "Комедия"))));
    }

    @Test
    public void updateFilmTest() {
        Optional<Film> filmOptional = filmStorage.findById(1L);
        assertThat(filmOptional).isPresent();

        Film film = filmOptional.get();
        String newName = "updated_film_title";
        String oldDescription = film.getDescription();
        Set<Genre> newGenres = Set.of(new Genre(3L, null));

        film.setName(newName);
        film.setGenres(newGenres);
        film.setDirectors(null);

        film = filmStorage.update(film);

        assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", newName)
                .hasFieldOrPropertyWithValue("description", oldDescription)
                .hasFieldOrProperty("genres")
                .satisfies(f -> assertThat(f.getGenres())
                        .satisfies(genres -> Assertions.assertEquals(1, genres.size()))
                        .satisfies(genres -> assertThat(genres.stream().findFirst())
                                .isPresent()
                                .satisfies(genre -> assertThat(genre.get())
                                        .hasFieldOrPropertyWithValue("id", 3L))));
    }
}
