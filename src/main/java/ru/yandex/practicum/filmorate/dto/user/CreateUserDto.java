package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class CreateUserDto {

    @Email
    @NotNull
    String email;

    @NotBlank
    String login;

    String name;

    @PastOrPresent
    LocalDate birthday;

}
