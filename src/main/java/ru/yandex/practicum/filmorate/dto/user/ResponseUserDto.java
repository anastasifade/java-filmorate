package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class ResponseUserDto {

    @NotBlank
    Long id;

    @NotBlank
    String name;

    LocalDate birthday;

}
