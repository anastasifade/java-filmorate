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
    private String email;

    @NotBlank
    private String login;

    @NullOrNotBlank
    private String name;

    @PastOrPresent
    private LocalDate birthday;

}
