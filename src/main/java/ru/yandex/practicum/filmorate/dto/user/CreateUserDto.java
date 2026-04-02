package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Value;
import ru.yandex.practicum.filmorate.validation.NullOrNotBlank;

import java.time.LocalDate;

@Value
public class CreateUserDto {

    @NotNull
    @Email
    String email;

    @NotBlank
    String login;

    @NullOrNotBlank
    String name;

    @PastOrPresent
    LocalDate birthday;

}
