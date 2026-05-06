package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({GenreDbStorage.class, GenreRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDbTests {

    private final GenreDbStorage genreStorage;

    @Test
    public void findGenreByIdTest() {
        Optional<Genre> genreOptional = genreStorage.findById(1);

        assertThat(genreOptional)
                .isPresent()
                .satisfies(genre -> assertThat(genre.get())
                        .hasFieldOrPropertyWithValue("id", 1L)
                        .hasFieldOrPropertyWithValue("name", "Комедия"));
    }

    @Test
    public void findAllGenreTest() {
        List<Genre> genres = genreStorage.findAll().stream().toList();
        Assertions.assertFalse(genres.isEmpty());

        genres.stream()
                .peek(genre -> assertThat(genre)
                        .hasFieldOrProperty("id")
                        .hasFieldOrProperty("name")
                        .satisfies(g -> assertThat(g.getId()).isNotNull())
                        .satisfies(g -> assertThat(g.getName()).isNotBlank()));
    }

}
