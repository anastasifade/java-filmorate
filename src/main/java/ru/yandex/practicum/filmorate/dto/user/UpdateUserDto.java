package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class UpdateUserDto {

    @NotNull
    Long id;

    String name;

    @Email
    String email;

    @PastOrPresent
    LocalDate birthday;

}
