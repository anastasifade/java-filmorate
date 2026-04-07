package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.validation.NullOrNotBlank;
import ru.yandex.practicum.filmorate.validation.film.ReleaseDateConstraint;

import java.time.LocalDate;

@Data
public class UpdateFilmDto {

    @NotNull(message = "Id must be provided.")
    private Long id;

    @NullOrNotBlank(message = "Cannot update to blank name.")
    private String name;
    @ReleaseDateConstraint(message = "Release date cannot be before 1985-12-28.")
    private LocalDate releaseDate;
    @Positive(message = "Duration must be a positive number.")
    private Integer duration;
    @Length(max = 200, message = "Description length must not exceed 200 characters.")
    private String description;

}
