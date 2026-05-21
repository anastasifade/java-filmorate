package ru.yandex.practicum.filmorate.dto.director;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class NewDirectorDto {
    private Long id;
    @NotBlank
    private String name;
}
