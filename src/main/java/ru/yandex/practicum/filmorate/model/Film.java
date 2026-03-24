package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.Duration;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = {"name", "releaseDate", "duration"})
@Builder
public class Film {

    private final Long id;
    private String title;
    private LocalDate releaseDate;
    private Duration duration;
    private String description;

}
