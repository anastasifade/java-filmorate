package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = {"id"})
@Builder
public class User implements Entity {

    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

}
