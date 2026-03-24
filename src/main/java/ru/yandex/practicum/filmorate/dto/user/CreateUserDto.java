package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

import java.time.LocalDate;

@Value
public class CreateUserDto {

    @Email
    String email;
    @NotBlank
    String login;
    String name;
    LocalDate birthday;

}
