package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Value;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Value
public class CreateFilmDto {
    @NotBlank(message = "Missing title.")
    String name;
    @NotNull(message = "Missing release date.")
    LocalDate releaseDate;
    @Positive(message = "Duration must be a positive number.")
    @NotNull(message = "Missing duration.")
    Integer duration;
    @Length(max = 200, message = "Description length cannot exceed 200 characters.")
    String description;
}
