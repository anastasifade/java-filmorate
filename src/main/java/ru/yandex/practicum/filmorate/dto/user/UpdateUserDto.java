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
    private Long id;

    @NullOrNotBlank
    private String name;

    @NullOrNotBlank
    private String login;

    @NullOrNotBlank
    @Email
    private String email;

    @PastOrPresent
    private LocalDate birthday;

}
