package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.time.LocalDate;

@Value
public class UpdateUserDto {

    @NotNull
    Long id;

    String name;
    String email;
    LocalDate birthday;

}
