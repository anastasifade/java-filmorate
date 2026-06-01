package ru.yandex.practicum.filmorate.mappers;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.Id;
import ru.yandex.practicum.filmorate.dto.director.NewDirectorDto;
import ru.yandex.practicum.filmorate.dto.director.ResponseDirectorDto;
import ru.yandex.practicum.filmorate.dto.director.UpdateDirectorDto;
import ru.yandex.practicum.filmorate.model.Director;

@Component
public final class DirectorMapper {

    private DirectorMapper() {
    }

    public static Director toDirectorFromId(Id id) {
        return Director.builder()
                .id(id.getId())
                .build();
    }

    public static Director toDirectorFromDto(NewDirectorDto dto) {
        return Director.builder()
                .name(dto.getName().trim())
                .build();
    }

    public static Director toDirectorFromDto(UpdateDirectorDto dto, Director director) {
        String name = dto.getName() == null ? director.getName() : dto.getName().trim();
        return Director.builder()
                .id(dto.getId())
                .name(name)
                .build();
    }

    public static ResponseDirectorDto toDto(Director director) {
        return ResponseDirectorDto.builder()
                .id(director.getId())
                .name(director.getName())
                .build();
    }
}
