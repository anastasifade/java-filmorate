package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id"})
@Builder
public class User implements Entity {

    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    private Set<Long> friends;

}
