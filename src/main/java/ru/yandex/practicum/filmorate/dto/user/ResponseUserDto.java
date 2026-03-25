package ru.yandex.practicum.filmorate.dto.user;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class ResponseUserDto {

    Long id;
    String name;
    LocalDate birthday;

}
