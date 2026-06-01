package ru.yandex.practicum.filmorate.dto.director;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Value;
import ru.yandex.practicum.filmorate.validation.NullOrNotBlank;

@Value
public class UpdateDirectorDto {

    @NotNull
    Long id;
    @NullOrNotBlank
    String name;

}
