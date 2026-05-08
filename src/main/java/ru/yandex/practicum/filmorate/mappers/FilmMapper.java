package ru.yandex.practicum.filmorate.mappers;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.Id;
import ru.yandex.practicum.filmorate.dto.film.ResponseFilmDto;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public final class FilmMapper {

    private FilmMapper() {
    }

    public static Film toFilm(NewFilmDto dto) {
        String name = dto.getName().trim();
        String description = dto.getDescription();
        if (dto.getDescription() != null) {
            description = description.trim();
        }

        Set<Genre> genres = dto.getGenres() == null ? null : toGenres(dto.getGenres());

        return Film.builder()
                .name(name)
                .releaseDate(dto.getReleaseDate())
                .description(description)
                .duration(dto.getDuration())
                .mpa(new MPA(dto.getMpa().getId(), null))
                .genres(genres)
                .build();
    }

    public static Film toFilm(UpdateFilmDto dto, Film film) {
        String name = (dto.getName() == null) ? film.getName() : dto.getName().trim();
        String description = (dto.getDescription() == null) ? film.getDescription() : dto.getDescription().trim();
        LocalDate release = (dto.getReleaseDate() == null) ? film.getReleaseDate() : dto.getReleaseDate();
        int duration = (dto.getDuration() == null) ? film.getDuration() : dto.getDuration();
        MPA mpa = (dto.getMpa() == null) ? film.getMpa() : new MPA(dto.getMpa().getId(), null);
        Set<Genre> genres = (dto.getGenres() == null || dto.getGenres().isEmpty()) ?
                film.getGenres() : toGenres(dto.getGenres());

        return Film.builder()
                .id(dto.getId())
                .name(name)
                .releaseDate(release)
                .description(description)
                .duration(duration)
                .mpa(mpa)
                .genres(genres)
                .build();
    }

    public static ResponseFilmDto toDto(Film film) {
        return ResponseFilmDto.builder()
                .id(film.getId())
                .name(film.getName())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .description(film.getDescription())
                .mpa(film.getMpa())
                .genres(film.getGenres())
                .build();
    }

    private static Set<Genre> toGenres(Set<Id> ids) {
        return ids.stream()
                .map(id -> new Genre(id.getId(), null))
                .collect(Collectors.toSet());
    }

}
