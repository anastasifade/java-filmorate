package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.dto.Id;
import ru.yandex.practicum.filmorate.validation.NullOrNotBlank;
import ru.yandex.practicum.filmorate.validation.film.ReleaseDateConstraint;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UpdateFilmDto {

    // TODO: add custom validators to rating and genre?

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
    private Id mpa;
    private Set<Id> genres;
    private Set<Id> directors;

}
