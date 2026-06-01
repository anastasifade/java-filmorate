package ru.yandex.practicum.filmorate.dto.film;

import lombok.Builder;
import lombok.Value;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.time.LocalDate;
import java.util.Set;

@Value
@Builder
public class ResponseFilmDto {
    Long id;
    String name;
    LocalDate releaseDate;
    int duration;
    String description;
    Set<Genre> genres;
    Set<Director> directors;
    MPA mpa;
}
