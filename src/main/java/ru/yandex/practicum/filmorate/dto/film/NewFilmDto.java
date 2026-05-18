package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Value;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.dto.Id;
import ru.yandex.practicum.filmorate.validation.film.ReleaseDateConstraint;

import java.time.LocalDate;
import java.util.Set;


@Value
public class NewFilmDto {

    @NotBlank(message = "Missing title.")
    private String name;
    @NotNull(message = "Missing release date.")
    @ReleaseDateConstraint(message = "Release date cannot be before 1985-12-28.")
    private LocalDate releaseDate;
    @Positive(message = "Duration must be a positive number.")
    @NotNull(message = "Missing duration.")
    private Integer duration;
    @Length(max = 200, message = "Description length cannot exceed 200 characters.")
    private String description;
    @NotNull
    private Id mpa;

    private Set<Id> genres;


}
