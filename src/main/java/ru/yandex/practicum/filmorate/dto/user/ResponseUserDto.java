package ru.yandex.practicum.filmorate.dto.user;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class ResponseUserDto {

    private Long id;
    private String name;
    private LocalDate birthday;

    // TODO: remove login and email from response user DTO to avoid sharing sensitive information

    private String login;
    private String email;

}
