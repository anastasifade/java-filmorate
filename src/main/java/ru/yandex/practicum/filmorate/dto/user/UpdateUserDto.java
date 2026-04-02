package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.NullOrNotBlank;

import java.time.LocalDate;

@Data
public class UpdateUserDto {

    @NotNull
    Long id;

    @NullOrNotBlank
    String name;

    @NullOrNotBlank
    String login;

    @NullOrNotBlank
    @Email
    String email;

    @PastOrPresent
    LocalDate birthday;

}
