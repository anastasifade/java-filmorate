package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.time.LocalDate;

@Value
public class UpdateFilmDto {

    @NotNull
    Long id;

    String title;
    LocalDate releaseDate;
    Integer duration;
    String description;

}
