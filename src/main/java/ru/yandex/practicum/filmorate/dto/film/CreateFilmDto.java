package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.time.LocalDate;

@Value
public class CreateFilmDto {
    @NotBlank(message = "Missing title.")
    String title;
    @NotNull(message = "Missing release date.")
    LocalDate releaseDate;
    @NotNull(message = "Missing duration.")
    Integer duration;
    String description;
}
