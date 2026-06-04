package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Value;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.dto.Id;
import ru.yandex.practicum.filmorate.validation.NullOrNotBlank;
import ru.yandex.practicum.filmorate.validation.film.ReleaseDateConstraint;

import java.time.LocalDate;
import java.util.Set;

@Value
public class UpdateFilmDto {
    @NotNull(message = "Id must be provided.")
    Long id;

    @NullOrNotBlank(message = "Cannot update to blank name.")
    String name;
    @ReleaseDateConstraint(message = "Release date cannot be before 1985-12-28.")
    LocalDate releaseDate;
    @Positive(message = "Duration must be a positive number.")
    Integer duration;
    @Length(max = 200, message = "Description length must not exceed 200 characters.")
    String description;
    Id mpa;
    Set<Id> genres;
    Set<Id> directors;
}
