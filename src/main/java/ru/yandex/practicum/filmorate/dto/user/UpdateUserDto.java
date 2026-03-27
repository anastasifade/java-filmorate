package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserDto {

    @NotNull
    Long id;

    String name;
    String login;

    @Email
    String email;

    @PastOrPresent
    LocalDate birthday;

}
