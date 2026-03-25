package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Value;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Value
@Builder
public class UpdateFilmDto {

    @NotNull
    Long id;

    String title;
    LocalDate releaseDate;
    @Positive(message = "Duration must be a positive number.")
    Integer duration;
    @Length(max = 200, message = "Description length must not exceed 200 characters.")
    String description;

}
