package ru.yandex.practicum.filmorate.dto.director;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Value
@Builder
public class ResponseDirectorDto {
    Long id;
    String name;
}
