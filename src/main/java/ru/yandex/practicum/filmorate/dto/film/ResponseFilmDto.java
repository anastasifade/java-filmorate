package ru.yandex.practicum.filmorate.dto.film;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class ResponseFilmDto {
    private Long id;
    private String name;
    private LocalDate releaseDate;
    private int duration;
    private String description;
    private Set<Genre> genres;
    private MPA mpa;
}
