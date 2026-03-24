package ru.yandex.practicum.filmorate.dto.film;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Value;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Duration;
import java.time.LocalDate;

@Value
public class CreateFilmDto {
    @NotBlank(message = "Missing title.")
    String title;
    @NotNull(message = "Missing release year.")
    LocalDate releaseDate;
    @NotNull(message = "Missing duration.")
    Duration duration;
    String description;
}
