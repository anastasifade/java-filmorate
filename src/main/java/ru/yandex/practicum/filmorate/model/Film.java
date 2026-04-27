package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id"})
@Builder
public class Film implements Entity {

    private Long id;
    private String name;
    private LocalDate releaseDate;
    private int duration;
    private String description;

    private Set<Long> likes;

}
