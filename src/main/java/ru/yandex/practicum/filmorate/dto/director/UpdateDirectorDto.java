package ru.yandex.practicum.filmorate.dto.director;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.NullOrNotBlank;

@Data
public class UpdateDirectorDto {

    @NotNull
    private Long id;
    @NullOrNotBlank
    private String name;

}
