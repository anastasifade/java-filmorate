package ru.yandex.practicum.filmorate.dto.film;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.time.Duration;
import java.time.LocalDate;

@Value
public class UpdateFilmDto {

    @NotNull
    Long id;

    String title;
    LocalDate releaseDate;
    Duration duration;
    String description;

}
